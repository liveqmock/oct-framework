package com.mopon.dao.master.sys;

import java.util.List;


import org.apache.ibatis.annotations.Param;
import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Repository;

import com.mopon.entity.member.Group;
import com.mopon.entity.member.GroupRole;

import com.mopon.entity.member.MenuItem;
import com.mopon.entity.member.Operate;
import com.mopon.entity.member.Role;
import com.mopon.entity.member.RoleMenu;
import com.mopon.entity.member.RoleOperate;
import com.mopon.entity.sys.QrtzLogInfo;

/**
 * 角色接口
 * @author liuguomin
 *
 */
@Repository
public interface IQrtzLogDao {


	/**
	 * 添加任务日志
	 * @param qrtzLogInfo
	 */
	public void save(QrtzLogInfo qrtzLogInfo) ;
	

	/**
	 * 查询任务日志列表
	 * @param role 查询条件
	 * @param startPage  起始记录数
	 * @param pageSize
	 * @return
	 */
	public List<QrtzLogInfo> queryQrtzLogs(@Param("qrtzLogInfo")QrtzLogInfo qrtzLogInfo,@Param("startPage")int startPage,@Param("pageSize")int pageSize);

	/**
	 * 查询总条数
	 * @param role 查询条件
	 * @return
	 */
	public Integer queryCount(@Param("qrtzLogInfo")QrtzLogInfo qrtzLogInfo);

}
