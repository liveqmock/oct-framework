package com.mopon.timer.mapping;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import com.mopon.entity.member.Task;
import com.mopon.entity.sys.SmsSend;
import com.mopon.service.impl.sys.QrtzServiceImpl;
import com.mopon.service.sys.ISysInitService;
import com.mopon.util.sms.API;

/**
 * quartz_config.xml初始化
 * @author liuguomin
 *
 */
@Component
public class InitQuartzConfigService implements ISysInitService {
	
	@Autowired
	private SchedulerFactoryBean startQuertz;
	
	private static Logger log = Logger.getLogger(InitQuartzConfigService.class);
	/**
	 * 保存mapping实体
	 * key:mapping.id
	 */
	public static Map<String,Mapping> mappings = new HashMap<String,Mapping>();
	
	//默认配置文件名称
	public static final String configName="config/quartz_config.xml";
	

	@Override
	public void initializer() throws Exception {
		log.info("start init quartz_config.xml");
		//配置文件名
		String configName =API.getSysconfigValue("quartz.config.url");
		if(StringUtils.isEmpty(configName))
			configName=this.configName;
		InputStream is = InitQuartzConfigService.class.getResourceAsStream("/"+configName);
		SAXReader reader =null;
		Document document = null;
		try {
			reader = new SAXReader();
			document = reader.read(is);
			List<Element> elements = document.getRootElement().elements();
			if(elements!=null && elements.size()>0){
				for(Element e : elements){
					Mapping mapping = new Mapping();
					if(e.getName().equals("mapping")){ 
						mapping.setId(e.attributeValue("id"));
						mapping.setName(e.attributeValue("name"));
						mappings.put(mapping.getId(), mapping);
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				try {is.close();} catch (IOException e) {}
		}
		//开启线程，定时检测任务状态
		Scheduler scheduler =startQuertz.getScheduler();	
		Thread thread = new Thread(new CheckTriggerStatus(scheduler));
		thread.start();
	}


}

/**
 * 定时检测trigger状态线程
 * @author liuguomin
 *每隔10分钟检测一次，如果状态异常，则发送短信
 */
class CheckTriggerStatus implements Runnable{

	private Scheduler startQuertz;
	public CheckTriggerStatus(Scheduler startQuertz){
		this.startQuertz=startQuertz;
	}
	@Override
	public void run() {
		while(true){
			try {
				List<Task> list =QrtzServiceImpl.getTaskList(startQuertz);
				if(list==null || list.size()==0)return;
				for(Task t :list){
					SmsSend smsSend = new SmsSend();
					smsSend.setAction("SEND");
					smsSend.setMobiles(API.getSysconfigValue("sms.mobile"));
					smsSend.setSiteName("业务平台");
					smsSend.setTimeing("0");
					smsSend.setType(SmsSend.TYPE_SMS);
					
					if(t.getStatus()==Trigger.STATE_NONE){
						smsSend.setContent("定时任务("+t.getTaskName()+")异常,找不到该任务!");
						API.sendSmsAPI(smsSend);
					}else if(t.getStatus()==Trigger.STATE_PAUSED){
						smsSend.setContent("定时任务("+t.getTaskName()+")没有运行!");
						API.sendSmsAPI(smsSend);
					}else if(t.getStatus()==Trigger.STATE_ERROR){
						smsSend.setContent("定时任务("+t.getTaskName()+")出现异常!");
						API.sendSmsAPI(smsSend);
					}
					//else if(t.getStatus()==Trigger.STATE_COMPLETE){
					//	System.out.println("complete");
					//}
					else if(t.getStatus()==Trigger.STATE_BLOCKED){
						smsSend.setContent("定时任务("+t.getTaskName()+")出现阻塞!");
						API.sendSmsAPI(smsSend);
					}
				}
				//每10分钟执行一次
				//Thread.sleep(1000*60*10);
				Thread.sleep(1000*60);
			} catch (Exception e) {}
		}
	}
	
}