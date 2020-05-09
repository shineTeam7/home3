package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameMO;

/** 玩家群移除成员 */
public class FuncSendRoleGroupRemoveMemberToPlayerMO extends FuncPlayerRoleGroupGameToGameMO
{
	/** 成员id */
	long playerID;
	/** 方式 */
	int type;
}
