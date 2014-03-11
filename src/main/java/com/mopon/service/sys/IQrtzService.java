package com.mopon.service.sys;

import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.mopon.entity.member.Group;
import com.mopon.entity.member.Role;
import com.mopon.entity.member.Task;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.QrtzLogInfo;
import com.mopon.entity.sys.SmsSend;

/**
 * 任务service 接口
 * @author liuguomin
 *
 */
public interface IQrtzService {
	
	/**
	 * 保存任务日志
	 * @param entity  任务日志对象
	 * @param smsSend 短信发送对象，如果为空，则不发送短信
	 * @throws Exception
	 */
	public void save(QrtzLogInfo entity, SmsSend smsSend) throws Exception;
	
	/**
	 * 查询任务日志
	 * @param entity
	 * @param pageNo
	 * @param pageSize
	 * @return
	 * @throws Exception
	 */
	public PageBean<QrtzLogInfo> queryQrtzLogs(QrtzLogInfo entity,int pageNo,int pageSize)throws Exception;
}
