package com.home.commonData.message.game.response.func.aution;

import com.home.commonData.message.game.response.func.base.FuncRMO;

/** 上架拍卖行物品消息 */
public class FuncAuctionSellItemMO extends FuncRMO
{
	/** 物品格子 */
	int index;
	/** 物品id */
	int itemID;
	/** 上架数目 */
	int num;
	/** 单价 */
	int onePrice;
}
