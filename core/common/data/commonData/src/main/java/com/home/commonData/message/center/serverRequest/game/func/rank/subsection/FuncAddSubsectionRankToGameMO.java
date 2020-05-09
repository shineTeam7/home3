package com.home.commonData.message.center.serverRequest.game.func.rank.subsection;

import com.home.commonData.data.social.rank.RankDO;

/** 推送增加排行榜数据消息 */
public class FuncAddSubsectionRankToGameMO
{
	/** 功能ID */
	int funcID;
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
	/** 排行数据 */
	RankDO data;
}
