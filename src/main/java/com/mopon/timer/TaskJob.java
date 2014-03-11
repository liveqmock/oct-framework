package com.mopon.timer;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.mopon.entity.sys.QrtzLogInfo;
import com.mopon.entity.sys.SmsSend;
import com.mopon.service.sys.IQrtzService;
import com.mopon.util.LoggerUtil;
import com.mopon.util.sms.API;

/**
 * <p>Description: 非序列化任务</p>
 * @date 2013年9月2日
 * @author reagan
 * @version 1.0
 * <p>Company:Mopon</p>
 * <p>Copyright:Copyright(c)2013</p>
 */
public class TaskJob {

	/**
	 * 定时任务队列
	 */
	private static List<ITaskHandler> taskHandlerList = new CopyOnWriteArrayList<ITaskHandler>();
	
	/**
	 * 日志类
	 */
	@Autowired
	private LoggerUtil loggerUtil; 
	
	@Autowired
	private IQrtzService qrtzService;
	
	/**
	 * 方法用途: 加入事件处理类<br>
	 * 实现步骤: <br>
	 * @param handler
	 */
	public static void addTaskHandler(ITaskHandler handler) {
		taskHandlerList.add(handler);
	}
	
	/**
	 * 方法用途: 执行任务<br>
	 * 实现步骤: 调用方法执行任务<br>
	 * 
	 */
	public void execute() {
		try{
			if(taskHandlerList != null) {
				loggerUtil.info("==========当前任务数 :" + taskHandlerList.size() + "==========");
				for(int i = 0; i < taskHandlerList.size(); i++) {
					ITaskHandler handler = taskHandlerList.get(i);
					handler.execute();
				}
			}
		}catch(Exception e){
			QrtzLogInfo log = new QrtzLogInfo();
			log.setLevel(2);
			log.setCreateTime(new Date());
			log.setDescription(e.getMessage());
			SmsSend smsSend = new SmsSend();
			smsSend.setAction("SEND");
			smsSend.setContent("定时任务异常，异常信息："+e.getMessage());
			smsSend.setMobiles(API.getSysconfigValue("sms.mobile"));
			smsSend.setSiteName("业务平台");
			smsSend.setTimeing("0");
			smsSend.setType(SmsSend.TYPE_SMS);
			try {
				qrtzService.save(log, smsSend);
			} catch (Exception e2) {}
		}
	}
}
