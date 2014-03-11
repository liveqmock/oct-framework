package com.mopon.controller.web.system;

import java.util.ArrayList;
import java.util.List;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import com.mopon.component.Component;
import com.mopon.entity.logs.ErrorLevel;
import com.mopon.entity.logs.ErrorMsg;
import com.mopon.entity.logs.ErrorStatus;
import com.mopon.entity.logs.ErrorType;
import com.mopon.entity.member.Task;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.QrtzLogInfo;
import com.mopon.entity.sys.ResultObject;
import com.mopon.exception.TaskException;
import com.mopon.service.impl.sys.QrtzServiceImpl;
import com.mopon.service.sys.IQrtzService;

/**
 * 定时任务控制层
 * @author liuguomin
 *
 */
@Controller
@RequestMapping("/task")
public class TaskController extends Component{

	@Autowired
	private SchedulerFactoryBean startQuertz;
	
	@Autowired
	private IQrtzService qrtzService;
	
	/**
	 * tigger 列表
	 * @param mav
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "triggerList")
	public ModelAndView triggerList(ModelAndView mav) {
		PageBean<Task> entity =new PageBean<Task>();

		List<Task> taskList = new ArrayList<Task>();
		try {
			Scheduler schedule = startQuertz.getScheduler();
			entity.setDataList(QrtzServiceImpl.getTaskList(schedule));
			entity.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.TIMERTASK.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new TaskException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("操作失败！");
		}
		
		mav.addObject("jsonData", entity);
		return mav;
	}
	
	/**
	 * 查询任务日志列表
	 * @param qrtzLogInfo
	 * @param page
	 * @param limit
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "qrtzLogList")
	public ModelAndView qrtzLogList(QrtzLogInfo qrtzLogInfo,  Integer page,Integer limit,ModelAndView mav) {
		PageBean<QrtzLogInfo> entity =new PageBean<QrtzLogInfo>();

		try {
			entity = qrtzService.queryQrtzLogs(qrtzLogInfo, page, limit);
			entity.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.TIMERTASK.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new TaskException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("操作失败！");
		}
		
		mav.addObject("jsonData", entity);
		return mav;
	}

	/**
	 * tigger暂停 恢复 停止
	 * @param tiggerName
	 * @param groupName
	 * @param status
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "triggerStatus")
	public ModelAndView doTriggerStatus(String tiggerName,String groupName,int status, ModelAndView mav) {
		ResultObject entity = new ResultObject();
		try {
			Scheduler schedule = startQuertz.getScheduler();
			if(0==status){//恢复
				schedule.resumeTrigger(tiggerName, groupName);
			}else if(1==status){ //暂停
				schedule.pauseTrigger(tiggerName, groupName);
			}
			entity.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.TIMERTASK.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new TaskException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("操作失败！");
		}
		mav.addObject("jsonData", entity);
		return mav;
	}
}
