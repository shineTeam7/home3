package com.home.commonData.data.social;

import com.home.commonData.data.role.RoleShowChangeDO;
import com.home.commonData.data.system.PlayerWorkDO;

/** 刷新部分角色数据在线事务数据 */
public class RefreshPartRoleShowDataWDO extends PlayerWorkDO
{
	/** 角色ID */
	long playerID;
	/** 改变数据 */
	RoleShowChangeDO data;
}
