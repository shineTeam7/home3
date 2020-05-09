package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.PlayerFuncWorkDO;
import com.home.commonData.data.item.ItemDO;
import com.home.shineData.support.MaybeNull;

/** 拍卖行回复购买物品数据 */
public class AuctionReBuyItemOWDO extends PlayerFuncWorkDO
{
	/** 实例id */
	long instanceID;
	/** 物品 */
	@MaybeNull
	ItemDO item;
	/** 信息码 */
	int code;
}
