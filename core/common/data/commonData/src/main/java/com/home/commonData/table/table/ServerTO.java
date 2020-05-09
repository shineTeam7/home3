package com.home.commonData.table.table;

import com.home.shineData.support.PrimaryKey;

/** 服务器信息表 */
public class ServerTO
{
	/** 创建时区服ID */
	@PrimaryKey
	int areaID;
	/** 当前区服ID */
	int nowAreaID;
	/** 服务器名(描述) */
	String name;
}
