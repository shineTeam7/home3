package com.home.commonData.data.social;

import com.home.commonData.data.system.PlayerWorkDO;
import com.home.shineData.support.MaybeNull;

/** 查询角色abs事务 */
public class QueryPlayerAWDO extends PlayerWorkDO
{
	/** 发起者角色ID */
	long sendPlayerID;
	/** 查询类型 */
	int type;
	/** 查询参数组 */
	@MaybeNull
	int[] args;
}
