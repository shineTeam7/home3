package com.home.commonData.message.game.request.func.rank;

import com.home.commonData.message.game.request.func.base.FuncSMO;

public class FuncRefreshRoleGroupRankMO extends FuncSMO
{
	/** 玩家群功能ID */
	int roleGroupFuncID;
	/** 玩家群ID */
	long groupID;
	/** 排行(-1为移除排行) */
	int rank;
	/** 排行值 */
	long value;
}
