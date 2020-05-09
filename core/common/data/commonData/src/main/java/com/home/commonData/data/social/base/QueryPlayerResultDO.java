package com.home.commonData.data.social.base;

import com.home.shineData.support.MaybeNull;

/** 查询角色结果数据 */
public class QueryPlayerResultDO
{
	/** 被查询角色ID */
	long queryPlayerID;
	/** 查询类型 */
	int queryType;
	/** 查询参数组 */
	@MaybeNull
	int[] queryArgs;
	/** 是否有该角色 */
	boolean isSuccess;
}
