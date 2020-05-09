package com.home.commonData.data.item.auction;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 拍卖行工具数据 */
public class AuctionToolDO extends FuncToolDO
{
	/** 物品交易记录组 */
	@MapKeyInValue("id")
	Map<Integer,AuctionItemRecordDO> itemRecords;
}
