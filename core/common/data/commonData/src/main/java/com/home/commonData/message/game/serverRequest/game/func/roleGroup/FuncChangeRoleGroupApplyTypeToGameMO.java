package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 更改玩家群申请方式 */
public class FuncChangeRoleGroupApplyTypeToGameMO extends FuncRoleGroupToGameMO
{
	/** 操作者 */
	long operator;
	
	/** 申请时是否可直接入群(无需同意) */
	boolean canApplyInAbs;
}
