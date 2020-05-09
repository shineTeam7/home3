package com.home.commonData.message.game.request.func.auction;

import com.home.commonData.data.item.auction.AuctionItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

import java.util.List;

/** 返回拍卖行查询结果 */
public class FuncAuctionReQueryMO extends FuncSMO
{
	/** 结果页码 */
	int page;
	/** 结果组 */
	List<AuctionItemDO> resultList;
}
