package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.role.RoleShowChangeDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 玩家群更新社交数据 */
public class FuncRoleGroupRefreshRoleShowDataToGameMO extends FuncRoleGroupToGameMO
{
	long playerID;
	
	RoleShowChangeDO data;
}
