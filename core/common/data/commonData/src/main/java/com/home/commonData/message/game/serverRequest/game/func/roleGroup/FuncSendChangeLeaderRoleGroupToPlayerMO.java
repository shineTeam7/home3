package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameMO;

/** 推送leader变化 */
public class FuncSendChangeLeaderRoleGroupToPlayerMO extends FuncPlayerRoleGroupGameToGameMO
{
	/** 上次队长 */
	long lastLeader;
	/** 现在的队长 */
	long nowLeader;
}
