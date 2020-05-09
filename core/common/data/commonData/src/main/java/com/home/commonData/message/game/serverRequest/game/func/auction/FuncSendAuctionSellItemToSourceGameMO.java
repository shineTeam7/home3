package com.home.commonData.message.game.serverRequest.game.func.auction;

import com.home.commonData.data.item.auction.AuctionItemDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 推送拍卖行出售物品到源服 */
public class FuncSendAuctionSellItemToSourceGameMO extends FuncToGameMO
{
	/** 出售物品 */
	AuctionItemDO data;
}
