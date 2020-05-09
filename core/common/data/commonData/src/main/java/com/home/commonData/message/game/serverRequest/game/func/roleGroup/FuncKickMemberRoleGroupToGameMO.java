package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 踢出成员 */
public class FuncKickMemberRoleGroupToGameMO extends FuncRoleGroupToGameMO
{
	/** 操作者 */
	long oprator;
	/** 目标 */
	long targetID;
}
