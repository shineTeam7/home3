package com.home.commonData.data.item.auction;

import com.home.commonData.data.system.CountDO;

import java.util.Queue;

/** 拍卖行物品记录数据 */
public class AuctionItemRecordDO
{
	/** 物品id */
	int id;
	/** 当前价格 */
	int price;
	/** 天数记录组 */
	Queue<CountDO> days;
}
