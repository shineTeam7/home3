package com.home.commonData.message.game.serverRequest.center.func.rank.subsection;

import com.home.commonData.message.game.serverRequest.center.func.base.FuncPlayerToCenterMO;
import com.home.shineData.support.MaybeNull;

/** 分段排行榜提交排行值到中心服 */
public class FuncCommitSubsectionRankValueToCenterMO extends FuncPlayerToCenterMO
{
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
	/** 版本号 */
	int version;
	/** 排行值 */
	long value;
	/** 参数组 */
	@MaybeNull
	long[] args;
}
