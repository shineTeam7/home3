package com.home.commonGame.tool.func;

import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.AuctionReSellItemOWData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.tool.func.AuctionTool;
import com.home.commonGame.global.GameC;
import com.home.commonGame.part.player.Player;
import com.home.shine.control.ThreadControl;
import com.home.shine.support.SnowFlaker;
import com.home.shine.support.func.ObjectCall;
import com.home.shine.table.DBConnect;

public class GameAuctionTool extends AuctionTool implements IGameAuctionTool
{
	private SnowFlaker _snowTool;
	
	public GameAuctionTool(int funcID,int auctionID)
	{
		super(funcID,auctionID);
	}
	
	@Override
	public void construct()
	{
		super.construct();
		
		_snowTool=new SnowFlaker(GameC.app.id);
	}
	
	public long getItemInstanceID()
	{
		return _snowTool.getOne();
	}
	
	@Override
	protected DBConnect getConnect()
	{
		return GameC.db.getCenterConnect();
	}
	
	@Override
	protected void sendToPlayerWorkData(long playerID,PlayerWorkData data)
	{
		GameC.main.addPlayerOfflineWork(playerID,data);
	}
	
	/** 添加执行器执行 */
	public void addExecutorFunc(long instanceID,ObjectCall<AuctionExecutor> func)
	{
		AuctionExecutor executor=getExecutor((int)instanceID & ThreadControl.poolThreadNumMark);
		
		executor.addFunc(()->
		{
			func.apply(executor);
		});
	}
}
