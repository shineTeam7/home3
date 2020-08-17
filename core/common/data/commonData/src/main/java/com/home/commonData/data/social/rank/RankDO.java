package com.home.commonData.data.social.rank;

import com.home.commonData.data.system.KeyDO;

/** 排行榜基础数据 */
public class RankDO extends KeyDO
{
	/** 排名(从1开始) */
	int rank;
	/** 排行值(如以后一个值不够用,再补,理论上应该是够的) */
	long value;
	/** 排行值刷新时间 */
	long valueRefreshTime;
}
