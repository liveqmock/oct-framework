package com.mopon.service.impl.sys;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;

import com.mopon.dao.master.sys.IQrtzLogDao;
import com.mopon.entity.member.Task;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.QrtzLogInfo;
import com.mopon.entity.sys.SmsSend;
import com.mopon.service.sys.IQrtzService;
import com.mopon.timer.mapping.InitQuartzConfigService;
import com.mopon.util.sms.API;

@Service("qrtzService")
public class QrtzServiceImpl implements IQrtzService{

	@Autowired
	private IQrtzLogDao qrtzDao;
	
	@Override
	public void save(QrtzLogInfo entity, SmsSend smsSend) throws Exception {
		qrtzDao.save(entity);
		if(smsSend!=null){
			API.sendSmsAPI(smsSend);
		}
	}

	@Override
	public PageBean<QrtzLogInfo> queryQrtzLogs(QrtzLogInfo entity, int pageNo, int pageSize)throws Exception {	
			PageBean<QrtzLogInfo> pageBean = new PageBean<QrtzLogInfo>();
	
			pageBean.setRecordCount(qrtzDao.queryCount(entity));
			pageBean.setPageSize(pageSize);
			pageBean.setPageCount(pageBean.getRecordCount());
			//设置当前页
			pageBean.setCurrentPage(pageNo>pageBean.getPageCount()?(int)pageBean.getPageCount():pageNo);
			pageBean.setCurrentPage(pageNo>0?pageNo:1);
			pageBean.setDataList(qrtzDao.queryQrtzLogs(entity,(pageBean.getCurrentPage()-1)*pageSize,pageSize));
			return pageBean;
	}
	
	/**
	 * 获取task 状态列表
	 * @param schedule
	 * @return 
	 * key：group名称<br>
     * value:tiggerList
	 * @throws SchedulerException
	 */
	public static List<Task> getTaskList(Scheduler schedule) throws SchedulerException{
		List<Task> taskList = new ArrayList<Task>();
		
		String[] groups = schedule.getTriggerGroupNames();
        for (int i = 0; i < groups.length; i++) {
        	List<String[]> triggerList = new ArrayList<String[]>();
        	//获取tigger
            String[] triggerNames = schedule.getTriggerNames(groups[i]);
            for (int j = 0; j < triggerNames.length; j++) {
            	int status  =schedule.getTriggerState(triggerNames[j], groups[i]);
            	Task task = new Task();
            	task.setGroupName(groups[i]);
            	task.setTaskId(triggerNames[j]);
            	task.setStatus(status);
            	task.setTaskName(InitQuartzConfigService.mappings.get(task.getTaskId()).getName());
            	taskList.add(task);
            }
        }	   
        return taskList;
	}

}
