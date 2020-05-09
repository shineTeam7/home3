package com.home.commonData.message.game.serverRequest.game.func.auction;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 取消上架拍卖行到源服 */
public class FuncSendAuctionCancelSellItemToSourceGameMO extends FuncToGameMO
{
	long playerID;
	
	long instanceID;
	
	int reason;
}
