package com.home.commonData.table.table;

import com.home.shineData.support.IndexKey;
import com.home.shineData.support.PrimaryKey;

/** 拍卖行表 */
public class AuctionItemTO
{
	@PrimaryKey
	long instanceID;
	/** 角色ID */
	@IndexKey("playerID")
	long playerID;
	/** 物品数据(其中的数目无用) */
	byte[] data;
	/** 上架时间 */
	long sellTime;
	/** 物品id */
	@IndexKey("itemID")
	int itemID;
	/** 物品等级 */
	@IndexKey("itemLevel")
	int itemLevel;
	/** 物品类型 */
	@IndexKey("itemType")
	int itemType;
	/** 物品第二类型 */
	@IndexKey("itemSecondType")
	int itemSecondType;
	/** 物品数目 */
	int itemNum;
	/** 物品单价 */
	int price;
}
