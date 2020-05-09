package com.home.commonData.message.game.serverRequest.game.func.rank;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameMO;

/** 更新排行(玩家群)消息 */
public class FuncRefreshRankForRoleGroupMO extends FuncPlayerRoleGroupGameToGameMO
{
	/** 排行榜的功能ID */
	int rankFuncID;
	/** 排行(-1为移除排行) */
	int rank;
	/** 排行值 */
	long value;
}
