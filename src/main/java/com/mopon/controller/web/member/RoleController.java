package com.mopon.controller.web.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import javax.servlet.http.HttpServletRequest;

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
import com.mopon.entity.member.MenuItem;
import com.mopon.entity.member.Operate;
import com.mopon.entity.member.Role;
import com.mopon.entity.member.RoleMenu;
import com.mopon.entity.member.RoleOperate;
import com.mopon.entity.sys.PageBean;
import com.mopon.entity.sys.Dictionary;
import com.mopon.entity.sys.ResultObject;
import com.mopon.entity.sys.ResultTree;
import com.mopon.exception.DatabaseException;
import com.mopon.exception.RoleException;
import com.mopon.service.member.IGroupService;
import com.mopon.service.member.IMemberService;
import com.mopon.service.member.IMenuItemService;
import com.mopon.service.member.IOperateService;
import com.mopon.service.member.IRoleService;
import com.mopon.service.sys.IDictionaryService;
import com.mopon.util.Base64Utils;


@Controller
@RequestMapping("/role")
public class RoleController extends Component {

	@Autowired
	private IRoleService roleService;

	@Autowired
	private IMenuItemService menuService;
	
	@Autowired
	private IMemberService memberService;
	
	@Autowired
	private IOperateService operateService;
	
	/**
	 * 分组服务对象
	 */
	@Autowired
	private IGroupService groupService;
	
	@Autowired
	private IDictionaryService dictionaryService;
	/**
	 * 角色列表:只能看该角色所在组和所有子组的所有角色，并且只能是该组分类和子分类的角色。
	 * 取消 findRole/{pageNo}/{pageSize} 方式
	 * @param response
	 * @param request
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "findRole", method = RequestMethod.GET)
	public ModelAndView findRole(HttpServletRequest request,Role role,  Integer page,Integer limit, ModelAndView mav) {
		PageBean<Role> entity = new PageBean<Role>();
		entity.setDateline(System.currentTimeMillis());
		try {
/*			if (role.getParentRoleId() == null ){// 默认查询顶级角色
				role.setParentRoleId(0);
			}*/
		//	role.setParentRoleId(member.getRole().getRoleId());//不能查看自己拥有的角色，只能查看自己拥有角色的子角色
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);	
			//修复返回上一级查看角色bug:
			if(role.getParentRoleId()!=null && role.getParentRoleId()==0)role.setParentRoleId(null);
			//修改为默认只查看顶级角色(取消)
			//if(role.getParentRoleId()==null)role.setParentRoleId(0);

			int myGroupType = member.getGroup().getGroupType();
			//过滤不属于子分组类型的分组
			List<Group> afterGroup =new ArrayList<Group>();
			//查询过滤，查询指定分组下所有角色
			if(role.getGroupId()!=null && role.getGroupId()!=0){
				Group g = new Group();
				g.setGroupId(role.getGroupId());
				afterGroup.add(g);
			}else{
				//获取当前用户的分组分类的子分类（不包含当前用户所在分组分类）
				//获取所有分组类型
				List<Role> roles = roleService.queryAllRoles(1);
				//子分组类型
				List<Role> myDictList = GroupController.getSubGrupType(myGroupType, roles) ;
				/*
				List<Integer> groupType = new ArrayList<Integer>();
				for(Role d: myDictList){
					groupType.add(d.getRoleId());
				}*/
				role.setGroupId(member.getRole().getGroupId());
				
				//查询所有子分组
				List<Group>  groupList = groupService.queryGroupByParentGroupId(member.getRole().getGroupId());
	
				for(Group g:groupList){
					for(Role r:myDictList){
						if(g.getGroupType().equals(r.getRoleId())){
							afterGroup.add(g);
							break;
						}
					}
				}
				role.setType(0);
				
				afterGroup.add(member.getGroup());
			}
			entity = roleService.queryRoles(role, page, limit,afterGroup);

			entity.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("查询角色失败！");

		} catch (Exception e) {
			System.out.println("role error");
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("查询角色失败！");
		}
		mav.addObject("jsonData", entity);
		mav.setViewName("role/list");
		return mav;
	}

	@RequestMapping(value = "findGroupType", method = RequestMethod.GET)
	public ModelAndView findGroupType(HttpServletRequest request,Role role,  Integer page,Integer limit, ModelAndView mav) {
		PageBean<Role> entity = new PageBean<Role>();
		entity.setDateline(System.currentTimeMillis());
		role.setType(1);
		try {
			entity = roleService.queryRoles(role, page, limit,null);
			entity.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("查询角色失败！");

		} catch (Exception e) {
			System.out.println("role error");
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			entity.setSuccess(false);
			entity.setMessage("查询角色失败！");
		}
		mav.addObject("jsonData", entity);
		mav.setViewName("role/list");
		return mav;
	}
	
	/**
	 * 查看角色
	 * 
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "findRole/{id}", method = RequestMethod.GET)
	public ModelAndView findRoleById(@PathVariable String id, ModelAndView mav) {
		int roleId = Integer.parseInt(id);
		ResultObject<Role> result = new ResultObject<Role>();
		Role role = null;
		try {
			role = roleService.queryRoleByRoleID(roleId);
			result.setResult(role);
			result.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("查看角色失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_QUERY.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("查看角色失败！");
		}
		mav.addObject("jsonData", result);
		mav.setViewName("role/view");
		return mav;
	}

	/**
	 * 进入修改角色界面
	 * 
	 * @param id
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "intoEditRole/{id}", method = RequestMethod.GET)
	public ModelAndView intoEditRoleById(@PathVariable String id,
			ModelAndView mav) {
		int roleId = Integer.parseInt(id);
		ResultObject<Role> result = new ResultObject<Role>();
		Role role = null;
		try {
			role = roleService.queryRoleByRoleID(roleId);
			result.setResult(role);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("修改角色失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_QUERY.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("修改角色失败！");
		}
		mav.addObject("jsonData", result);
		mav.setViewName("role/edit");
		return mav;
	}

	/**
	 * 进入添加角色界面
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "intoAddRole/{parentRoleId}", method = RequestMethod.GET)
	public ModelAndView intoAddRole(HttpServletRequest request,@PathVariable Integer parentRoleId,
			ModelAndView mav) {
		try {
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);
			
			if(member.getUserName().equals("admin")){//如果是admin管理员，则加载所有信息
				parentRoleId =0;
			}else if(parentRoleId==null || parentRoleId==0){//如果传递的父类角色ID为null或者0，则设置父类id为当前登陆用户的角色ID					
				parentRoleId = member.getRole().getRoleId();
			}
			mav.addObject("jsonData", parentRoleId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_ADD.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		mav.setViewName("role/add");
		return mav;
	}

	/**
	 * 添加角色
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "addRole", method = RequestMethod.POST)
	public ModelAndView addRole(HttpServletRequest request, Role role,String[] listCheckbox, ModelAndView mav) {
		ResultObject<Role> result = new ResultObject<Role>();
		try {
			role.setCreateDate(new Date());
			String username = Base64Utils.decodeToString(cookieManager
					.readCookie("userName", request));
			role.setCreateUser(username);
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);
			
			//如果没有选择父角色，则为添加子角色功能
			if(role.getParentRoleId()==null || role.getParentRoleId()==0){
				role.setParentRoleId(member.getRole().getRoleId());
				role.setGroupId(member.getRole().getGroupId());
			}else{
				Role parentRole = roleService.queryRoleByRoleID(role.getParentRoleId());
				role.setGroupId(parentRole.getGroupId());
			}
			
			// 保存菜单ID list
			List<Integer> menuList = new ArrayList<Integer>();
			// 保存操作ID list
			List<Integer> operateList = new ArrayList<Integer>();
			if (listCheckbox != null && listCheckbox.length > 0) {
				for (String ids : listCheckbox) {
					if (ids.contains("OP"))
						operateList.add(Integer.parseInt(ids.substring(2,
								ids.length())));
					else
						menuList.add(Integer.parseInt(ids));
				}
			}
			role.setType(0);
			int flag =roleService.save(role, menuList, operateList, 0);
			if(flag==2){
				result.setSuccess(false);
				result.setMessage("添加角色失败，角色名("+role.getRoleName()+")存在");
				mav.addObject("jsonData", result);
				return mav;
			}
			result.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("添加角色失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_ADD.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("添加角色失败！");
		}
		mav.addObject("jsonData", result);
		return mav;
	}
	
	/**
	 * 添加角色
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "addGroupType", method = RequestMethod.POST)
	public ModelAndView addGroupType(HttpServletRequest request, Role role,String[] listCheckbox, ModelAndView mav) {
		ResultObject<Role> result = new ResultObject<Role>();
		try {
			role.setType(1);
			role.setCreateDate(new Date());
			String username = Base64Utils.decodeToString(cookieManager
					.readCookie("userName", request));
			role.setCreateUser(username);
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);
			
			if(role.getParentRoleId()==null ){ //默认添加角色为顶级角色
				role.setParentRoleId(0);
			}
			
			// 保存菜单ID list
			List<Integer> menuList = new ArrayList<Integer>();
			// 保存操作ID list
			List<Integer> operateList = new ArrayList<Integer>();
			if (listCheckbox != null && listCheckbox.length > 0) {
				for (String ids : listCheckbox) {
					if (ids.contains("OP"))
						operateList.add(Integer.parseInt(ids.substring(2,
								ids.length())));
					else
						menuList.add(Integer.parseInt(ids));
				}
			}
			role.setType(1);
			int flag =roleService.save(role, menuList, operateList, 0);
			if(flag==2){
				result.setSuccess(false);
				result.setMessage("添加类型失败，类型名("+role.getRoleName()+")存在");
				mav.addObject("jsonData", result);
				return mav;
			}
			result.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("添加类型失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_ADD.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("添加类型失败！");
		}
		mav.addObject("jsonData", result);
		return mav;
	}

	/**
	 * 更新角色
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "editRole", method = RequestMethod.POST)
	public ModelAndView editRole(Role role,String[] listCheckbox,String[] delMenuOperateIds, ModelAndView mav) {
		ResultObject<Role> result = new ResultObject<Role>();
		try {
			// 保存菜单ID list
			List<Integer> addMenuList = new ArrayList<Integer>();
			// 保存操作ID list
			List<Integer> addOperateList = new ArrayList<Integer>();
			
			// 要删除的菜单ID list
			List<Integer> delMenuList = new ArrayList<Integer>();
			// 要删除的操作ID list
			List<Integer> delOperateList = new ArrayList<Integer>();
			
			if(listCheckbox!=null){
				for (String ids : listCheckbox) {
					if (ids.contains("OP"))
						addOperateList.add(Integer.parseInt(ids.substring(2,ids.length())));
					else
						addMenuList.add(Integer.parseInt(ids));
				}
			}
			
			if(delMenuOperateIds!=null){
				for (String ids : delMenuOperateIds) {
					if (ids.contains("OP"))
						delOperateList.add(Integer.parseInt(ids.substring(2,ids.length())));
					else
						delMenuList.add(Integer.parseInt(ids));
				}
			}
			roleService.update(role, addMenuList,delMenuList, addOperateList,delOperateList);
			result.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("更新角色失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_EDIT.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("更新角色失败！");
		}
		mav.addObject("jsonData", result);
		return mav;
	}

	/**
	 * 删除角色
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "delRole", method = RequestMethod.POST)
	public ModelAndView delRole(int[] listCheckbox, ModelAndView mav) {
		ResultObject<Role> result = new ResultObject<Role>();
		try {
			
			Group group = new Group();
			for(Integer groupTypeId:listCheckbox){
				group.setGroupType(groupTypeId);
				group=groupService.queryGroupForObject(group);
				if(group!=null){
					result.setSuccess(false);
					result.setMessage("删除失败！该类型已有对应分组,不允许删除");
					mav.addObject("jsonData", result);
					return mav;
				}
			}
			
			roleService.remove(listCheckbox);
			result.setSuccess(true);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
			result.setSuccess(false);
			result.setMessage("删除失败！");
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_REMOVE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
			result.setSuccess(false);
			result.setMessage("删除失败！");
		}
		mav.addObject("jsonData", result);
		// mav.setViewName("role/findRole");
		return mav;
	}

	/**
	 * 根据ID删除角色组
	 * 
	 * @param ids
	 *            角色组ID集合
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "delGroupRoleById/{ids}", method = RequestMethod.DELETE)
	public ModelAndView delGroupRoleById(@PathVariable int[] ids,
			ModelAndView mav) {
		try {
			for (int i = 0; i < ids.length; i++) {
				// int roleId = Integer.parseInt(ids[i]);
				roleService.removeRoleGroupById(ids[i]);
			}
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(
					ErrorStatus.ROLE_DELETE_GROUP.value(), e.getMessage(),
					ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 添加角色菜单
	 * 
	 * @param role
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "addRoleMenu", method = RequestMethod.POST)
	public ModelAndView addRoleMenu(RoleMenu roleMenu, ModelAndView mav) {
		try {
			roleService.saveRoleMenu(roleMenu);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.ROLE_ADD_MENU.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 添加角色操作
	 * 
	 * @param roleOperate
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "addRoleOperate", method = RequestMethod.POST)
	public ModelAndView addRoleMenu(RoleOperate roleOperate, ModelAndView mav) {
		try {
			roleService.saveRoleOperate(roleOperate);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(
					ErrorStatus.ROLE_ADD_OPERATE.value(), e.getMessage(),
					ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 根据角色ID查询菜单
	 * 
	 * @param roleId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "findMenuByRoleId/{roleId}", method = RequestMethod.GET)
	public ModelAndView findMenuByRoleId(@PathVariable String roleId,
			ModelAndView mav) {
		List<MenuItem> list = new ArrayList<MenuItem>();
		try {
			list = roleService.queryMenuByRoleID(Integer.parseInt(roleId));
			mav.addObject("jsonData", list);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(
					ErrorStatus.ROLE_MENU_QUERY.value(), e.getMessage(),
					ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 根据角色ID查询组
	 * 
	 * @param roleId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "findGroupByRoleId/{roleId}", method = RequestMethod.GET)
	public ModelAndView findGroupByRoleId(@PathVariable String roleId,
			ModelAndView mav) {
		List<Group> list = new ArrayList<Group>();
		try {
			list = roleService.queryGroupByRoleID(Integer.parseInt(roleId));
			mav.addObject("jsonData", list);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(
					ErrorStatus.ROLE_GROUP_QUERY.value(), e.getMessage(),
					ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 根据角色ID查询操作
	 * 
	 * @param roleId
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "findOperateByRoleId/{roleId}", method = RequestMethod.GET)
	public ModelAndView findOperateByRoleId(@PathVariable String roleId,
			ModelAndView mav) {
		List<Operate> list = new ArrayList<Operate>();
		try {
			list = roleService.queryOperateByRoleID(Integer.parseInt(roleId));
			mav.addObject("jsonData", list);
		} catch (DataAccessException e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(ErrorStatus.DATABASE.value(),
					e.getMessage(), ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new DatabaseException(errorMsg);
		} catch (Exception e) {
			e.printStackTrace();
			ErrorMsg errorMsg = new ErrorMsg(
					ErrorStatus.ROLE_OPERATE_QUERY.value(), e.getMessage(),
					ErrorLevel.ERROR.value(),
					ErrorType.APPLICATION_ERROR.value());
			new RoleException(errorMsg);
		}
		return mav;
	}

	/**
	 * 获取角色树
	 * 
	 * @param roleId
	 *            如果传递分组ID,还要查询该分组下所有的角色<br>
	 *            如果传递菜单ID,还要查询该菜单下所有角色
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "getRoleTree", method = RequestMethod.GET)
	public ModelAndView getRoleTree(HttpServletRequest request,Integer groupId, Integer menuId,ModelAndView mav) {
		try {
			ResultTree<Object> result = new ResultTree<Object>();
			Map<String, Object> resultMap = new HashMap<>();
			
			//从session中获取member信息
			Member member = memberService.findCachedMember(request);
			//从数据库中获取最新member信息
			member = memberService.queryMemberById(member);
			int parentID = member.getRole().getRoleId();
				
			//查询当前用户所拥有的角色
			List<Role> roleList= roleService.queryRoleIdByParentRoleId(parentID);

			
			//如果是admin，则加载所有角色
			if("admin".equals(member.getUserName())){
				roleList = roleService.queryAllRoles(0);
				parentID =0;
			}else{ //把自己角色加进去
				roleList.add(member.getRole());
			}

			//该组下所有角色
			List<Role> myRoleGroup = null;
			//该菜单下所有角色
			List<Role> myRoleMenu = null;
			
			//根据组id查询该组下所有角色
			if (groupId != null && groupId != 0) {
				myRoleGroup = roleService.queryRoleByGroupID(groupId);
			}
			//根据菜单id查询该菜单下所有角色
			if (menuId != null && menuId != 0) {
				myRoleMenu = roleService.queryRoleByMenuID(menuId);
			}
			
			
			//组装 tree
			Map<String, Object> roleMap = roleService.getRoleMap(roleList,parentID,myRoleGroup,myRoleMenu);

			result.add(roleMap);

			mav.addObject("jsonData", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}

	/**
	 * 获取菜单树
	 * @param loadAllMenuOperate  是否加载所有菜单和操作，  1 是    ,0  或者 null 否(默认)。当等于1时。parentRoleId 无效
	 * @param parentRoleId 角色ID，加载该角色拥有的菜单  为null加载所有
	 * @param loadsubMenu  是否加载节点菜单(拥有操作的那个菜单)  null 或者1 加载，0 不加载
	 * @param loadOperate 加载菜单时，是否加载菜单拥有的操作 : <br> null 或者 1  加载   ，0 不加载<br>
	 * 					备注：只有加载节点菜单 ，此参数才有效
	 * @param showChecked  是否显示复选框按钮   null 或者 1 显示   0 不显示
	 * @param roleId 
	 *            如果传递子角色ID，还要查询该角色的所有菜单，用于复选框是否选择中状态
	 * @param roleIdtype 角色类型对应的角色ID。对应分组类型ID 用来过滤机构菜单和角色
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "getMenuTree", method = RequestMethod.GET)
	public ModelAndView getMenuTree(HttpServletRequest request,Integer loadAllMenuOperate,Integer roleId,Integer showChecked,Integer loadsubMenu,Integer loadOperate,Integer parentRoleId,Integer roleIdtype, ModelAndView mav) {
		try {
			
			boolean isshowchecked = showChecked ==null || showChecked ==1; 
			boolean isloadsubmenu = loadsubMenu ==null || loadsubMenu ==1;
			boolean isloadoperate =loadOperate==null || loadOperate==1;
			boolean isloadAllMenuOperate =loadAllMenuOperate!=null && loadAllMenuOperate==1;
			ResultTree<Object> result = new ResultTree<Object>();
			// 查询所有菜单
			List<MenuItem> menuList = null;
			//加载角色的操作
			List<Operate> operateList = null;
			//角色拥有的菜单
			List<MenuItem> myMenu = null;
			//角色拥有的操作
			List<Operate> myOperate = null;
			
			Group myGroup = null;
			if(roleId!=null && roleId!=0 && (parentRoleId ==null|| parentRoleId==0) && !isloadAllMenuOperate){ //编辑顶级角色
				Role role =roleService.queryRoleByRoleID(roleId);//要编辑的角色
				Group g = new Group();
				g.setGroupId(role.getGroupId());
				myGroup =groupService.getGroupDetail(g);
				menuList =roleService.queryMenuByRoleID(myGroup.getGroupType());
				operateList = roleService.queryOperateByRoleID(myGroup.getGroupType());
				myMenu = roleService.queryMenuByRoleID(roleId);
				myOperate = roleService.queryOperateByRoleID(roleId);
			}else{
				//从session中获取member信息
				Member member = memberService.findCachedMember(request);
				//从数据库中获取最新member信息
				member = memberService.queryMemberById(member);
				if(isloadAllMenuOperate||member.getRole()==null){ //加载所有
					parentRoleId=null;
				//	if(roleId==null)
						menuList =menuService.queryAllMenuItem();
				//	else
				//		menuList =roleService.queryMenuByRoleID(myGroup.getGroupType());
				}else{
					if(parentRoleId==null||parentRoleId==0) //默认添加子角色
						parentRoleId = member.getRole().getRoleId();
					
					menuList = roleService.queryMenuByRoleID(parentRoleId);
					
				}	
						
		
				// 如果存在roleId,查询该组的所有菜单和操作
				if (roleId != null && roleId != 0) {
					myMenu = roleService.queryMenuByRoleID(roleId);
					if(isloadoperate){ //加载操作，所以也查询角色拥有的操作
						myOperate = roleService.queryOperateByRoleID(roleId);
						if(parentRoleId!=null&&parentRoleId!=0)
							operateList = roleService.queryOperateByRoleID(parentRoleId);
						else{ //如果父类角色ID为0,则查询所有操作
						//	if(loadAllMenuOperate!=null && loadAllMenuOperate==1)
						//		operateList = roleService.queryOperateByRoleID(myGroup.getGroupType());
						//	else
								operateList = operateService.getOperateByRoleId(null);
						}
					}
				}else{ //加载父类所有操作
					if(isloadoperate){ 
						if(parentRoleId!=null&&parentRoleId!=0)
							operateList = roleService.queryOperateByRoleID(parentRoleId);
						else//如果父类角色ID为0,则查询所有操作
							operateList = operateService.getOperateByRoleId(null);
					}
				}				
				
			}
			
			//过滤菜单和操作：只显示该角色所在组分类所有操作和菜单（不包含本角色所在组分类）
			//查询所有角色类型的子类型

			if(roleIdtype!=null){
				////////////////菜单过滤//////////////
				List<MenuItem> allSubTypeMenu = new ArrayList<MenuItem>();
				List<MenuItem> ownerSubTypeMenu = new ArrayList<MenuItem>();
				List<Operate> allSubTypeOperate = new ArrayList<Operate>();
				List<Operate> ownerSubTypeOperate = new ArrayList<Operate>();

				//查询所有菜单
				allSubTypeMenu.addAll(roleService.queryMenuByRoleID(roleIdtype));
				allSubTypeOperate.addAll(roleService.queryOperateByRoleID(roleIdtype));
				
				//开始过滤allSubTypeList中没有的菜单
				for(int i=0;i<menuList.size();i++){
					Integer menuId = menuList.get(i).getMenuId();
					for( int j =0;j<allSubTypeMenu.size();j++){
						if(menuId.equals(allSubTypeMenu.get(j).getMenuId())){
							ownerSubTypeMenu.add(menuList.get(i));
							break;
						}
					}
				}
				menuList = ownerSubTypeMenu;
				////////////////操作过滤//////////////
				for(int i=0;i<operateList.size();i++){
					Integer opId = operateList.get(i).getOpId();
					for( int j =0;j<allSubTypeOperate.size();j++){
						if(opId.equals(allSubTypeOperate.get(j).getOpId())){
							ownerSubTypeOperate.add(operateList.get(i));
							break;
						}
					}
				}
				operateList = ownerSubTypeOperate;
			}

			
			// 将菜单按tree格式放入map中
			Map<String, Object> menuMap = roleService.getMenuMap(menuList,myMenu,myOperate,true,operateList,isshowchecked,isloadsubmenu,isloadoperate);
			
			result.add(menuMap);
		
			mav.addObject("jsonData", result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mav;
	}

	/**
	 * demo
	 * 
	 * @param mav
	 * @return
	 */
	@RequestMapping(value = "demoExtTree")
	public ModelAndView demoExtTree(ModelAndView mav) {
		/*
		 * ResultMap result = new ResultMap(); Map<String,Object> resultMap =
		 * new HashMap<>();
		 * 
		 * result.setResultMap(resultMap); mav.addObject("jsonData", result);
		 */

		ResultTree<Object> result = new ResultTree<Object>();
		Map map1 = new HashMap();

			List child = new ArrayList();
			Map map1_1 = new HashMap();
			map1_1.put("text", "dfdf");
			map1_1.put("leaf", true);
			map1_1.put("checked", true);
			child.add(map1_1);
	
			Map map1_2 = new HashMap();
			map1_2.put("text", "dfdf");
			map1_2.put("leaf", true);
			map1_2.put("checked", true);
			child.add(map1_2);

	//	map1.put("cls", "folder");
		map1.put("expanded", false);
		map1.put("text", "Grocery List");
		map1.put("children", child);
		map1.put("checked", true);
		// map2
		Map map2 = new HashMap();

			List child2 = new ArrayList();
			Map map2_1 = new HashMap();
			map2_1.put("text", "dfdf");
			map2_1.put("leaf", true);
			map2_1.put("checked", true);
			child2.add(map2_1);
	
			Map map2_2 = new HashMap();
			map2_2.put("text", "dfdf");
			map2_2.put("leaf", true);
			map2_2.put("checked", true);
			child2.add(map2_2);

	//	map2.put("cls", "folder");
		map2.put("expanded", false);
		map2.put("text", "Energy foods");
		map2.put("children", child2);
		map2.put("checked", true);

		
		result.add(map1);
		result.add(map2);
		mav.addObject("jsonData", result);
		return mav;

	}
}
