package com.home.commonData.message.game.serverRequest.game.func.auction;

import com.home.commonData.data.item.auction.AuctionBuyItemDO;
import com.home.commonData.data.role.RoleSimpleShowDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 拍卖行购买物品到源服 */
public class FuncSendAuctionBuyItemToSourceGameMO extends FuncToGameMO
{
	/** 玩家数据 */
	RoleSimpleShowDO roleData;
	
	AuctionBuyItemDO data;
}
