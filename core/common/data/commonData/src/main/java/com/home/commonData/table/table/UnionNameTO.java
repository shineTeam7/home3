package com.home.commonData.table.table;

import com.home.shineData.support.PrimaryKey;

/** 工会名字表 */
public class UnionNameTO
{
	/** 名字 */
	@PrimaryKey
	String name;
	/** 角色id */
	long groupID;
}
