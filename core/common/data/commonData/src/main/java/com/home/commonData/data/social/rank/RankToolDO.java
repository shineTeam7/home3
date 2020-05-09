package com.home.commonData.data.social.rank;

import com.home.commonData.data.func.FuncToolDO;

import java.util.List;

/** 排行榜数据(存库数据) */
public class RankToolDO extends FuncToolDO
{
	/** 当前排行版本(刷新一次一个版本) */
	int version;
	/** 列表 */
	List<RankDO> list;
}
