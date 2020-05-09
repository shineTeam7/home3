package com.home.commonBase.tool.func;

import com.home.commonBase.data.social.rank.RankData;
import com.home.shine.support.collection.SList;

/** 排行数据 */
public interface IRankTool
{
	/** 获取排行数据 */
	SList<RankData> getList();
	/** 获取版本 */
	int getVersion();
	/** 提交排行数据(返回排名) */
	void commitRank(int version,long key,long value,long[] args);
	/** 移除排行数据 */
	void removeRankData(int version,long key);
	/** 获取匹配值下限 */
	long getValueLimit();
	/** 获取排行 */
	int getRank(long key);
	/** 获取排行数据 */
	RankData getRankData(long key);
	
	/** 移除当前版本的排行数据 */
	default void removeRankData(long key)
	{
		removeRankData(getVersion(),key);
	}
}
