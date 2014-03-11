package com.mopon.controller.web.system;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mopon.component.Component;
import com.mopon.entity.logs.ErrorLevel;
import com.mopon.entity.logs.ErrorMsg;
import com.mopon.entity.logs.ErrorStatus;
import com.mopon.entity.logs.ErrorType;
import com.mopon.entity.sys.Config;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.ResultObject;
import com.mopon.exception.ConfigException;
import com.mopon.exception.DatabaseException;
import com.mopon.service.sys.IConfigService;

@Controller
@RequestMapping("/config")
public class ConfigController extends Component{

	@Autowired
	private IConfigService configService;
	
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public ModelAndView add(Config config, ModelAndView mav) {
		ResultObject<Config> ro = new ResultObject<Config>();
		try {
			Integer count = configService.queryForKeyCount(config);
			if(count>0){
	            ro.setSuccess(false);
	            ro.setMessage("配置键重复，请重新输入！");
			}else{
				config.setDate(new Date());
				configService.setConfig(config);
				ro.setSuccess(true);
			}
		} catch (ConfigException e) {
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.CONFIG_SAVE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
		}
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	@RequestMapping(value = "update", method = RequestMethod.POST)
	public ModelAndView update(Config config, ModelAndView mav) {
		ResultObject<Config> ro = new ResultObject<Config>();
		try {
			configService.modifiConfig(config);
			ro.setSuccess(true);
		} catch (ConfigException e) {
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.CONFIG_SAVE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new ConfigException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
		}
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	@RequestMapping(value = "remove", method = RequestMethod.GET)
	public ModelAndView remove(String[] ids, ModelAndView mav) {
		ResultObject<Config> ro = new ResultObject<Config>();
		try {
			configService.removeConfig(ids);
			ro.setSuccess(true);
		} catch (ConfigException e) {
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.CONFIG_SAVE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new ConfigException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加配置失败！");
		}
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	@RequestMapping(value = "list")
	public ModelAndView list(ModelAndView mav,Config config,Integer page,Integer limit) {
		PageBean<Config> rl = new PageBean<Config>();
		rl.setDateline(System.currentTimeMillis());
		try {
			rl = configService.queryConfigForList(page,limit,config);
			rl.setSuccess(true);
		} catch (ConfigException e) {
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.CONFIG_SAVE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new ConfigException(errorMsg);
            rl.setSuccess(false);
            rl.setMessage("查找配置失败！");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            rl.setSuccess(false);
            rl.setMessage("查找配置失败！");
		}
		mav.addObject("jsonData", rl);
		return mav;
	}
	
}
