package com.home.commonData.message.center.serverRequest.game.func.auction;

import com.home.commonData.message.center.serverRequest.game.func.base.FuncCenterToGameMO;

import java.util.Map;

/** 刷新拍卖行物品价格消息到逻辑服 */
public class FuncRefreshAuctionItemPriceToGameMO extends FuncCenterToGameMO
{
	/** 物品价格列表 */
	Map<Integer,Integer> itemPriceDic;
}
