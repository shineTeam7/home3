package com.home.commonData.table.table;

import com.home.shineData.support.AutoIncrementKey;
import com.home.shineData.support.PrimaryKey;

/** 白名单表 */
public class WhiteListTO
{
	/** 用户ID */
	@PrimaryKey
	@AutoIncrementKey
	int id;
	/** 类型 */
	int type;
	/** 值 */
	String value;
}
