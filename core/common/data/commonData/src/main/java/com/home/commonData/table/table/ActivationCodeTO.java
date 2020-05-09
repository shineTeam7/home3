package com.home.commonData.table.table;

import com.home.shineData.support.PrimaryKey;

/** 激活码表 */
public class ActivationCodeTO
{
	/** 码 */
	@PrimaryKey
	String code;
	/** id */
	int id;
	/** 剩余可使用次数 */
	int lastNum;
	/** 生成时间戳 */
	long bornTime;
	/** 失效时间戳 */
	long disableTime;
}
