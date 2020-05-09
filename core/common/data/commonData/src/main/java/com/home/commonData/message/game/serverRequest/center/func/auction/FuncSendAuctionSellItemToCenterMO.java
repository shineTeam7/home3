package com.home.commonData.message.game.serverRequest.center.func.auction;

import com.home.commonData.data.item.auction.AuctionItemDO;
import com.home.commonData.message.game.serverRequest.center.func.base.FuncPlayerToCenterMO;

/** 推送出售物品到中心服消息 */
public class FuncSendAuctionSellItemToCenterMO extends FuncPlayerToCenterMO
{
	/** 出售物品 */
	AuctionItemDO data;
}
