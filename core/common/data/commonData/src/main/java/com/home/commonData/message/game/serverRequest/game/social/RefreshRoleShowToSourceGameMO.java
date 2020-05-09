package com.home.commonData.message.game.serverRequest.game.social;

import com.home.commonData.data.role.RoleShowChangeDO;

/** 更新角色显示数据到源服 */
public class RefreshRoleShowToSourceGameMO
{
	/** 角色ID */
	long playerID;
	/** 改变数据 */
	RoleShowChangeDO data;
}
