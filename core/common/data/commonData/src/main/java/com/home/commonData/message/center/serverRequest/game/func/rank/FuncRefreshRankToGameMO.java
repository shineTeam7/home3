package com.home.commonData.message.center.serverRequest.game.func.rank;

import com.home.shineData.support.MaybeNull;

/** 推送更新排行榜数据消息 */
public class FuncRefreshRankToGameMO
{
	/** 功能ID */
	int funcID;
	/** 主键 */
	long key;
	/** 排行 */
	int rank;
	/** 排行值 */
	long value;
	/** 参数组 */
	@MaybeNull
	long[] args;
}
