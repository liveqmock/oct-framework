package com.mopon.controller.web.member;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mopon.component.Component;
import com.mopon.entity.logs.ErrorLevel;
import com.mopon.entity.logs.ErrorMsg;
import com.mopon.entity.logs.ErrorStatus;
import com.mopon.entity.logs.ErrorType;
import com.mopon.entity.member.Group;
import com.mopon.entity.member.Member;
import com.mopon.entity.member.Role;
import com.mopon.entity.sys.Dictionary;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.ResultList;
import com.mopon.entity.sys.ResultObject;
import com.mopon.entity.sys.ResultTree;
import com.mopon.exception.DatabaseException;
import com.mopon.exception.GroupException;
import com.mopon.service.member.IGroupService;
import com.mopon.service.member.IMemberService;
import com.mopon.service.member.IRoleService;
import com.mopon.service.sys.IDictionaryService;
import com.mopon.util.Base64Utils;


/**
 * <p>Description: 分组控制器类</p>
 * @date 2013年9月22日
 * @author reagan
 * @version 1.0
 * <p>Company:Mopon</p>
 * <p>Copyright:Copyright(c)2013</p>
 */
@Controller
@RequestMapping(value = "/group")
public class GroupController extends Component {

	private static Logger log = Logger.getLogger(GroupController.class);
	/**
	 * 分组服务对象
	 */
	@Autowired
	private IGroupService groupService;
	
	/**
	 * 角色服务对象
	 */
	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IMemberService memberService;
	@Autowired
	private IDictionaryService dictionaryService;
	/**
	 * 进入添加组界面
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "add", method = RequestMethod.GET)
	public ModelAndView intoAdd(ModelAndView mav) {
		mav.setViewName("group/add");
		return mav;
	}
	
	
	@RequestMapping(value = "add", method = RequestMethod.POST)
	public ModelAndView add(HttpServletRequest request, Group group, ModelAndView mav) {
		ResultObject<Group> ro = new ResultObject<Group>();
		try {
			group.setDate(new Date());
			String username = Base64Utils.decodeToString(cookieManager.readCookie("userName", request));	

			if("admin".equals(username) && group.getParentGroupId()==null){
					group.setParentGroupId(0);
			}else{ 
				//普通用户不允许添加顶级分组
				if(group.getParentGroupId()==null){
					ro.setSuccess(false);
					ro.setMessage("添加分组失败，请选择父分组！");
					mav.addObject("jsonData",ro);
					return mav;
				}
			}
			group.setUsername(username);
			groupService.addGroup(group, null);

		}catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加组失败！");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_ADD.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("添加组失败！");
		} 
		mav.addObject("jsonData",ro);
		return mav;
	}
	
	/**
	 * 进入编辑组页面
	 * @param groupId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "edit/{groupId}", method = RequestMethod.GET)
	public ModelAndView intoEdit(@PathVariable int groupId, ModelAndView mav) {
		Group group = new Group();
		group.setGroupId(groupId);
		ResultObject<Group> ro = new ResultObject<Group>();
		try {
			group = groupService.getGroupDetail(group);
			ro.setSuccess(true);
			ro.setResult(group);
		}catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_EDIT.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} 
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	/**
	 * 编辑组信息
	 * @param group
	 * @param roids
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "edit", method = RequestMethod.POST)
	public ModelAndView edit(Group group, ModelAndView mav) {
		ResultObject<Group> ro = new ResultObject<Group>();
		try {

			groupService.editGroup(group, null,null);
			ro.setSuccess(true);
		}catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_EDIT.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} 
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	/**
	 * 查看组
	 * @param groupId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "detail/{groupId}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable int groupId, ModelAndView mav) {
		ResultObject<Group> ro = new ResultObject<Group>();
		try {
			ro.setSuccess(true);
			Group group = new Group();
			group.setGroupId(groupId);
			group = groupService.getGroupDetail(group);
			ro.setResult(group);
			
		}catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_DETAIL.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("编辑组失败！");
		} 
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	/**
	 * 删除组
	 * @param listCheckbox
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "remove", method = RequestMethod.POST)
	public ModelAndView remove(int[] listCheckbox, ModelAndView mav) {
		ResultObject<Group> ro = new ResultObject<Group>();
		try {
			Group group = new Group();
			for(int i=0;i<listCheckbox.length;i++){
				group.setGroupId(listCheckbox[i]);
				groupService.removeGroup(group);
			}
			ro.setSuccess(true);
		}catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("删除组失败！");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_REMOVE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            ro.setSuccess(false);
            ro.setMessage("删除组失败！");
		} 
		mav.addObject("jsonData", ro);
		return mav;
	}
	
	@RequestMapping(value = "query", method = RequestMethod.GET)
	public ModelAndView query(HttpServletRequest request,Group group,int page,int limit, ModelAndView mav) {
		PageBean<Group> pageBean = new PageBean<Group>();
		pageBean.setDateline(System.currentTimeMillis());
		try {
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);	
			//int myGroupId = member.getGroup().getGroupId();
			//所有子分组ID
			List<Group> supGroupList = null;
			//如果是非管理员，查询所拥有的子分组
			if(!"admin".equals(member.getUserName())){ 
				supGroupList =groupService.queryGroupByParentGroupId(member.getGroup().getGroupId());
			}
			
			pageBean = groupService.getGroupForList(group, page, limit, 1,supGroupList);
			
			
			pageBean.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new DatabaseException(errorMsg);
            pageBean.setSuccess(false);
            pageBean.setMessage("查询失败");
		} catch (Exception e) {
            e.printStackTrace();
            ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.GROUP_QUERY.value(),e.getMessage(),ErrorLevel.ERROR.value(),ErrorType.APPLICATION_ERROR.value());
            new GroupException(errorMsg);
            pageBean.setSuccess(false);
            pageBean.setMessage("查询失败");
		} 
		mav.addObject("jsonData", pageBean);
		return mav;
	}
	
	/**
	 * 获取分组树
	 * @param request
	 * @param groupType  查询该分组
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "getGroupTree", method = RequestMethod.GET)
	public ModelAndView getGroupTree(HttpServletRequest request,Integer groupType,ModelAndView mav) {
		try{
			ResultTree<Object> result = new ResultTree<Object>();
			Map<String, Object> resultMap = new HashMap<>();
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);	
			
			int myGroupId = member.getGroup().getGroupId();
			//要加载的所有子分组
			List<Group> groupList = new ArrayList<Group>();

			Group query = new Group();
			query.setParentGroupId(myGroupId);
			
			//如果是admin用户，则查询所有组
			if("admin".equals(member.getUserName())){
				query.setParentGroupId(null);
				groupList.addAll(groupService.queryGroupForList(query));
			}else{  //不是admin，把自己拥有的分组加进去
				groupList.add(member.getGroup());
				groupList.addAll(groupService.queryGroupByParentGroupId(member.getGroup().getGroupId()));
			}
			

			//组装group
			Map<String,Object> map = groupService.getGroupMap(groupList, member.getGroup().getGroupId());
			
			result.add(map);
			mav.addObject("jsonData", result);
		}catch(Exception e){
			
		}	
		return mav;
	}
	
	/**
	 * 获取所有分组类型
	 * @param request
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "getGroupTypeList", method = RequestMethod.GET)
	public ModelAndView getGroupTypeList(HttpServletRequest request,ModelAndView mav) {
		try{
			ResultList<Role> result = new ResultList<Role>();
			result.setSuccess(true);
			Map<String, Object> resultMap = new HashMap<>();
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);	
			
			int myGroupId = member.getGroup().getGroupId();
			int myGroupType = member.getGroup().getGroupType();
			
			//获取当前用户的分组分类的子分类（不包含当前用户所在分组分类）
			//获取所有分组类型
			List<Role> roles = roleService.queryAllRoles(1);
			//子分组类型
		//	List<Role> myDictList = getSubGrupType(myGroupType, roles) ;	
		
			result.setResultList(roles);
			mav.addObject("jsonData", result);
		}catch(Exception e){
			
		}	
		return mav;
	}
	
	
	/**
	 * 获取所有子分组列表
	 * @param request
	 * @param groupType
	 * @param includeMyGroup 是否包含本身所在分组   0或者null  不包含  (默认) ; 1包含
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "getGroupList", method = RequestMethod.GET)
	public ModelAndView getGroupList(HttpServletRequest request,Integer includeMyGroup,ModelAndView mav) {
		try{
			ResultList<Group> result = new ResultList<Group>();
			result.setSuccess(true);
			Map<String, Object> resultMap = new HashMap<>();
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);	
			
			int myGroupId = member.getGroup().getGroupId();
			Integer myGroupType = member.getGroup().getGroupType();
			
			//获取当前用户的分组分类的子分类（不包含当前用户所在分组分类）
			//获取所有分组类型
			List<Role> roles = roleService.queryAllRoles(1);
			//子分组类型
			List<Role> myDictList = getSubGrupType(myGroupType, roles) ;	
			//把自己所在分组加进去
			if(includeMyGroup!=null && includeMyGroup==1){
				for(Role r: roles){
					if(myGroupType.equals(r.getRoleId()))
						myDictList.add(r);
				}
			}
			
			List<Integer> myDictList2 = new  ArrayList<Integer>();
			for(Role r:myDictList){
				myDictList2.add(r.getRoleId());
			}
			log.info("get group list size:"+myDictList.size());
			log.info("myGroupType:"+myGroupType);
			//
			//子分组类型下分组
			List<Group> supGroupList = new ArrayList<Group>();

			Group query = new Group();
			query.setParentGroupId(myGroupId);
			
			//如果是admin用户，则查询所有组
			if("admin".equals(member.getUserName())){
				query.setParentGroupId(null);
				supGroupList.addAll(groupService.queryGroupForList(query));
			}else{  //不是admin，把自己拥有的分组加进去
				//要加载的所有子分组
				List<Group> groupList = new ArrayList<Group>();
				groupList.add(member.getGroup());
				groupList.addAll(groupService.queryGroupByParentGroupId(member.getGroup().getGroupId()));
				//过滤不是子分组类型的分组
				for(Group g : groupList){
					if(myDictList2.contains(g.getGroupType()))
						supGroupList.add(g);
				}
				log.info("is not admin into....");
			}

			result.setResultList(supGroupList);
			mav.addObject("jsonData", result);
		}catch(Exception e){
			
		}	
		return mav;
	}
	
	/**
	 * 获取子分组类型
	 * @param groupType 分组类型
	 * @param list
	 * @return
	 */
	public static List<Role> getSubGrupType(Integer groupType,List<Role> allList){
		List<Role> allSubList = new ArrayList<Role>();
		
		List<Role> subList = new ArrayList<Role>();
		if(groupType==null) return subList;
		for(Role d:allList){
			if(groupType.equals(d.getParentRoleId())){
				subList.add(d);
			}
		}
		allSubList.addAll(subList);
		for(Role d:subList){
			allSubList.addAll(getSubGrupType(d.getRoleId(), allList));
		}
		return allSubList;
	}
}
