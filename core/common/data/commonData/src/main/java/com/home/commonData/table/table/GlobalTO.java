package com.home.commonData.table.table;

import com.home.shineData.data.DateDO;
import com.home.shineData.support.PrimaryKey;

/** 全局表 */
public class GlobalTO
{
	/** 默认主键 */
	@PrimaryKey
	int key;
	/** 数据结构版本 */
	int dataVersion;
	/** 上次保存时间 */
	DateDO saveDate;
	/** blob值 */
	byte[] data;
}
