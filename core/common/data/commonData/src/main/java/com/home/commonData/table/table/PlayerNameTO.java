package com.home.commonData.table.table;

import com.home.shineData.support.IndexKey;
import com.home.shineData.support.PrimaryKey;

/** 中心服角色名字表 */
public class PlayerNameTO
{
	/** 名字 */
	@PrimaryKey
	String name;
	/** 角色id */
	long playerID;
}
