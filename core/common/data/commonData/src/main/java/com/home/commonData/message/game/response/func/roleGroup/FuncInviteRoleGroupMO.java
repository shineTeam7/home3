package com.home.commonData.message.game.response.func.roleGroup;

import com.home.commonData.message.game.response.func.base.FuncRMO;

/** 邀请加入玩家群消息 */
public class FuncInviteRoleGroupMO extends FuncRMO
{
	/** 玩家群id(-1为使用邀请直接创建) */
	long groupID;
	/** 玩家id */
	long playerID;
}
