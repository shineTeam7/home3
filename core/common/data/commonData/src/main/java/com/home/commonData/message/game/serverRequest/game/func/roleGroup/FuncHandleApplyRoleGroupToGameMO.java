package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 处理结果到玩家群 */
public class FuncHandleApplyRoleGroupToGameMO extends FuncRoleGroupToGameMO
{
	/** 操作者id */
	long operator;
	/** 目标角色id */
	long target;
	/** 结果 */
	int result;
}
