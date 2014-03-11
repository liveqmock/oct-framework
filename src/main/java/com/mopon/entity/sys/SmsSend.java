package com.mopon.entity.sys;

/**
 * 短信对象
 * @author liuguomin
 *
 */
public class SmsSend {

	public static String TYPE_SMS = "SMS";
	public static String TYPE_MMS = "MMS";
	/**
	 * 操作类型“SEND"
	 */
	private String action ;
	/**
	 * 短信类型SMS MMS
	 */
	private String type;
	/**
	 * 业务系统名称
	 */
	private String siteName;
	/**
	 * 短信内容
	 */
	private String content;
	/**
	 * 号码单个或多个字符串，号分隔
	 */
	private String mobiles;
	/**
	 * 是否定时定时格式字符串 YYYYmmDDhhMMss
	 */
	private String timeing;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getMobiles() {
		return mobiles;
	}
	public void setMobiles(String mobiles) {
		this.mobiles = mobiles;
	}
	public String getTimeing() {
		return timeing;
	}
	public void setTimeing(String timeing) {
		this.timeing = timeing;
	}

}
