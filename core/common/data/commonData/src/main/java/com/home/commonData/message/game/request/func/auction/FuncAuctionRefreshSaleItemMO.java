package com.home.commonData.message.game.request.func.auction;

import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 拍卖行更新自己出售物品数目消息 */
public class FuncAuctionRefreshSaleItemMO extends FuncSMO
{
	/** 实例ID */
	long instanceID;
	/** 更新后的数目 */
	int num;
}
