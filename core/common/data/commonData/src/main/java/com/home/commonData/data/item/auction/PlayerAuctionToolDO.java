package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 玩家拍卖行工具数据 */
public class PlayerAuctionToolDO extends FuncToolDO
{
	/** 已上架的物品组 */
	@MapKeyInValue("instanceID")
	Map<Long,AuctionItemDO> sellItems;
	/** 上架中的物品组 */
	@MapKeyInValue("instanceID")
	Map<Long,AuctionItemDO> preSellItems;
	/** 购买中的物品 */
	@MapKeyInValue("instanceID")
	Map<Long,AuctionBuyItemDO> preBuyItems;
}
