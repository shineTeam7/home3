package com.home.commonData.message.game.request.func.auction;

import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 回复获取拍卖行物品推荐价格 */
public class FuncReGetAuctionItemSuggestPriceMO extends FuncSMO
{
	/** 物品id */
	int itemID;
	/** 单价 */
	int price;
}
