package com.home.commonData.data.social.rank;

import com.home.commonData.data.func.FuncToolDO;

/** 简单排名数据 */
public class RankSimpleDO extends FuncToolDO
{
	/** 排名(从1开始) */
	int rank;
	/** 排行值(如以后一个值不够用,再补,理论上应该是够的) */
	long value;
}
