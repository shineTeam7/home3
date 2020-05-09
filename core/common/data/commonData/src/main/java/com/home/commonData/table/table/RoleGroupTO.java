package com.home.commonData.table.table;

import com.home.shineData.data.DateDO;
import com.home.shineData.support.PrimaryKey;

/** 玩家群表 */
public class RoleGroupTO
{
	/** 群id */
	@PrimaryKey
	long groupID;
	/** 原区ID */
	int createAreaID;
	/** 功能id */
	int funcID;
	/** 创建日期 */
	DateDO createDate;
	/** 数据 */
	byte[] data;
}
