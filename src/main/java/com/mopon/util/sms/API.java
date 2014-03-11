package com.mopon.util.sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mopon.controller.httpclient.HttpProcess;

import com.mopon.entity.sys.MailTask;
import com.mopon.entity.sys.SmsSend;
import com.mopon.util.MailUtil;
import com.mopon.util.PropertiesUtil;

/**
 * 远程API接口调用
 * @author liuguomin
 *
 */
public class API {

	private static Logger log = Logger.getLogger(API.class);

	public static void main(String[] args) {

	}

	
	/**
	 * 发送短信API
	 * @param siteName 业务系统
	 * @param smsContent 短信内容
	 * @param mobiles 手机号 多个手机号，用";"隔开
	 * @return
	 */
	public static boolean  sendSmsAPI(SmsSend smsSend){
		boolean status = false;
		try{
			//调用短信接口
			Map<String, String> params = new HashMap<String, String>();
			//号码列表可以一个也可以多个
			//操作类型“SEND"
			params.put("action",smsSend.getAction());
			//短信类型SMS MMS
			params.put("type",smsSend.getType());
			//业务系统名称
			params.put("siteName", smsSend.getSiteName());
			//短信内容（需要有模板才能修改）
			params.put("content", smsSend.getContent());
			//号码单个或多个字符串，号分隔
			params.put("mobiles", smsSend.getMobiles());
			//是否定时定时格式字符串 YYYYmmDDhhMMss
			params.put("timeing", smsSend.getTimeing());
			HttpProcess process = new HttpProcess();
			String smsUrl = getSysconfigValue("sms.url");
			byte[] bytes = process.doPost(smsUrl, params);
			String responseBody = new String(bytes, "UTF-8");	
			status = "SUCCESS".equals(responseBody)?true:false;
		}catch(Exception e){
			status = false;
			log.error("远程调用发送短信接口错误",e);
		}
		return status;
	}
	
	/**
	 * 获取sysconfig.properites文件指定值
	 * @param key
	 * @return
	 */
	public static String getSysconfigValue(String key){
		PropertiesUtil propertiesUtil = new PropertiesUtil("config/sysconfig.properties");
		return propertiesUtil.getProperty(key);
	}
	
	public static void sendEmailAPI(MailUtil sendMail,List<MailTask> list){
		if(sendMail==null){
			ApplicationContext ctx = new ClassPathXmlApplicationContext("/config/spring/mail.xml");  
			sendMail = (MailUtil) ctx.getBean("mail");	
		}
		PropertiesUtil config = new PropertiesUtil("config/sysconfig.properties");
		String from = config.getProperty("mail.from");
		if(list!=null && list.size()>0){
			for(MailTask mt : list){
				mt.setFrom(from);
				sendMail.addMailTaskQueue(mt);
			}
		}	
	}

}
