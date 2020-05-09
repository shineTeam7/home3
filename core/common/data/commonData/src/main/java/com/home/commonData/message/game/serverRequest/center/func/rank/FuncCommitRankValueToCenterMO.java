package com.home.commonData.message.game.serverRequest.center.func.rank;

import com.home.commonData.message.game.serverRequest.center.func.base.FuncPlayerToCenterMO;
import com.home.shineData.support.MaybeNull;

/** 排行榜提交排行值到中心服 */
public class FuncCommitRankValueToCenterMO extends FuncPlayerToCenterMO
{
	/** 版本号 */
	int version;
	/** 排行值 */
	long value;
	/** 参数组 */
	@MaybeNull
	long[] args;
}
