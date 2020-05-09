package com.home.commonData.message.game.request.func.auction;

import com.home.commonData.data.item.auction.AuctionItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 拍卖行添加自己的出售物品消息 */
public class FuncAuctionAddSaleItemMO extends FuncSMO
{
	/** 出售物品数据 */
	AuctionItemDO data;
}
