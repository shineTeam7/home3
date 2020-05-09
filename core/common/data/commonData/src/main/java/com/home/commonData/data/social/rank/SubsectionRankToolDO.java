package com.home.commonData.data.social.rank;

import com.home.commonData.data.func.FuncToolDO;

import java.util.List;
import java.util.Map;

/** 分段排行榜数据(存库数据) */
public class SubsectionRankToolDO extends FuncToolDO
{
	/** 当前排行版本(刷新一次一个版本) */
	int version;
	/** 大组index,小组index,排行榜列表 */
	Map<Integer,List<List<RankDO>>> listListMap;
}
