package com.home.commonData.message.game.serverRequest.game.func.rank;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;
import com.home.shineData.support.MaybeNull;

/** 提交排行数据到源服 */
public class FuncCommitRankValueToSourceGameMO extends FuncToGameMO
{
	/** 角色id */
	long playerID;
	/** 版本号 */
	int version;
	/** 排行值 */
	long value;
	/** 参数组 */
	@MaybeNull
	long[] args;
}
