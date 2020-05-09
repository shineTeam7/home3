package com.home.commonData.message.game.serverRequest.center.func.auction;

import com.home.commonData.data.item.auction.AuctionBuyItemDO;
import com.home.commonData.data.role.RoleSimpleShowDO;
import com.home.commonData.message.game.serverRequest.center.func.base.FuncPlayerToCenterMO;

/** 拍卖行购买物品到中心服 */
public class FuncSendAuctionBuyItemToCenterMO extends FuncPlayerToCenterMO
{
	/** 玩家数据 */
	RoleSimpleShowDO roleData;
	
	AuctionBuyItemDO data;
}
