package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.PlayerFuncWorkDO;
import com.home.commonData.data.role.RoleSimpleShowDO;

/** 更新出售物品数目 */
public class AuctionSoldItemOWDO extends PlayerFuncWorkDO
{
	/** 实例ID */
	long instanceID;
	/** 购买者 */
	RoleSimpleShowDO consumer;
	/** 出售数目 */
	int num;
	/** 是否售罄 */
	boolean isSoldOut;
}
