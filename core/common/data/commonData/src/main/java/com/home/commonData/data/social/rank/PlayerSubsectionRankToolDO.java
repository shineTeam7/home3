package com.home.commonData.data.social.rank;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

/** 角色排行工具数据 */
public class PlayerSubsectionRankToolDO extends FuncToolDO
{
	/** 版本 */
	int version;
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
	/** 匹配值 */
	long value;
	/** 参数组 */
	@MaybeNull
	long[] args;
}
