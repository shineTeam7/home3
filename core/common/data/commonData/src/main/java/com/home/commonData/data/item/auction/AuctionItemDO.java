package com.home.commonData.data.item.auction;

import com.home.commonData.data.item.ItemDO;

/** 拍卖物品数据 */
public class AuctionItemDO
{
	/** 实例id */
	long instanceID;
	/** 角色ID */
	long playerID;
	/** 物品数据 */
	ItemDO data;
	/** 上架时间 */
	long sellTime;
	/** 单价 */
	int price;
}
