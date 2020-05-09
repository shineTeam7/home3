package com.home.commonData.message.game.response.func.aution;

import com.home.commonData.message.game.response.func.base.FuncRMO;
import com.home.shineData.support.MessageUseMainThread;

/** 获取拍卖行物品推荐价格 */
@MessageUseMainThread
public class FuncGetAuctionItemSuggestPriceMO extends FuncRMO
{
	int itemID;
}
