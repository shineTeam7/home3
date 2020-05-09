package com.home.commonData.message.center.serverRequest.game.func.rank.subsection;

import com.home.shineData.support.MaybeNull;

/** 推送更新排行榜数据消息 */
public class FuncRefreshSubsectionRankToGameMO
{
	/** 功能ID */
	int funcID;
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
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
