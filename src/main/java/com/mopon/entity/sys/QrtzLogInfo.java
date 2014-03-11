package com.mopon.entity.sys;

import java.util.Date;

/**
 * 定时任务日志
 * @author liuguomin
 *
 */
public class QrtzLogInfo {

	private Integer logId;
	private Integer level;
	private String description;
	private Date createTime;

	public Integer getLogId() {
		return logId;
	}
	public void setLogId(Integer logId) {
		this.logId = logId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}
