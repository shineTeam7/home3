package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.PlayerFuncWorkDO;

/** 拍卖行移除上架物品 */
public class AuctionRemoveSellItemOWDO extends PlayerFuncWorkDO
{
	/** 实例id */
	long instanceID;
	/** 剩余数目 */
	int lastNum;
	/** 原因 */
	int code;
}
