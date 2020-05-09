package com.home.commonData.message.game.response.func.aution;

import com.home.commonData.data.item.auction.AuctionQueryConditionDO;
import com.home.commonData.message.game.response.func.base.FuncRMO;
import com.home.shineData.support.MaybeNull;

/** 拍卖行查询 */
public class FuncAuctionQueryMO extends FuncRMO
{
	/** 查询条件组(如为空则取上次条件组) */
	@MaybeNull
	AuctionQueryConditionDO[] conditions;
	/** 结果页码 */
	int page;
}
