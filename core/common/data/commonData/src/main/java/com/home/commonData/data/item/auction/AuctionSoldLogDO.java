package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.FuncInfoLogDO;
import com.home.commonData.data.item.ItemDO;
import com.home.commonData.data.role.RoleSimpleShowDO;

/** 拍卖行出售物品日志 */
public class AuctionSoldLogDO extends FuncInfoLogDO
{
	/** 购买者信息 */
	RoleSimpleShowDO role;
	/** 物品信息 */
	ItemDO item;
	/** 单价 */
	int price;
}
