package com.home.commonData.data.item.auction;

import com.home.commonData.data.item.auction.AuctionQueryConditionDO;

/** 整形拍卖行查询条件 */
public class IntAuctionQueryConditionDO extends AuctionQueryConditionDO
{
	/** 下限(如相等，则用等号) */
	int min;
	/** 上限 */
	int max;
}
