package com.home.commonCenter.tool.func;

import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.AuctionReSellItemOWData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.tool.func.AuctionTool;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.serverRequest.game.func.auction.FuncRefreshAuctionItemPriceToGameServerRequest;
import com.home.shine.table.DBConnect;

public class CenterAuctionTool extends AuctionTool
{
	public CenterAuctionTool(int funcID,int auctionID)
	{
		super(funcID,auctionID);
	}
	
	@Override
	protected DBConnect getConnect()
	{
		return CenterC.db.getConnect();
	}
	
	@Override
	protected void sendItemPriceChanged()
	{
		CenterC.server.radioGames(FuncRefreshAuctionItemPriceToGameServerRequest.create(_funcID,createItemPriceDic()));
	}
	
	@Override
	protected void sendToPlayerWorkData(long playerID,PlayerWorkData data)
	{
		CenterC.main.addPlayerOfflineWork(playerID,data);
	}
}
