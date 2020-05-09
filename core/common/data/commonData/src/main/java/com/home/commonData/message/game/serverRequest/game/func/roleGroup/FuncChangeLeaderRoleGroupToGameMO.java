package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 禅让群主 */
public class FuncChangeLeaderRoleGroupToGameMO extends FuncRoleGroupToGameMO
{
	/** 自身 */
	long selfID;
	/** 目标id */
	long playerID;
}
