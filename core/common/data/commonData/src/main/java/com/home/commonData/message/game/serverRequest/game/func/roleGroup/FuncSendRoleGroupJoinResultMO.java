package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerGameToGameMO;

/** 推送加入玩家群类消息结果 */
public class FuncSendRoleGroupJoinResultMO extends FuncPlayerGameToGameMO
{
	long groupID;
	
	boolean success;
}
