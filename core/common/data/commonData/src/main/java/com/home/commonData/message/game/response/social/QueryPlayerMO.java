package com.home.commonData.message.game.response.social;

import com.home.shineData.support.MaybeNull;

/** 客户端查询角色消息 */
public class QueryPlayerMO
{
	/** 目标角色ID */
	long playerID;
	/** 查询类型 */
	int type;
	/** 查询参数 */
	@MaybeNull
	int[] args;
}
