package com.home.commonData.table.table;

import com.home.shineData.data.DateDO;
import com.home.shineData.support.PrimaryKey;
import com.home.shineData.support.UseReplaceInsteadOfInsert;

/** 角色社交数据表 */
@UseReplaceInsteadOfInsert
public class RoleSocialTO
{
	/** 角色id */
	@PrimaryKey
	long playerID;
	/** 数据 */
	byte[] data;
}
