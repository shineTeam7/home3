package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.AuctionConfig;
import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.data.item.auction.GameAuctionToolData;
import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.global.GameC;
import com.home.shine.support.SnowFlaker;
import com.home.shine.support.collection.IntIntMap;

public class GameToCenterAuctionTool extends FuncTool implements IGameAuctionTool
{
	private AuctionConfig _config;
	
	private GameAuctionToolData _data;
	
	private SnowFlaker _snowTool;
	
	public GameToCenterAuctionTool(int funcID,int auctionID)
	{
		super(FuncToolType.Auction,funcID);
		
		_config=AuctionConfig.get(auctionID);
	}
	
	@Override
	public void construct()
	{
		super.construct();
		
		_snowTool=new SnowFlaker(GameC.app.id);
	}
	
	@Override
	public long getItemInstanceID()
	{
		return _snowTool.getOne();
	}
	
	@Override
	public int getItemPrice(int id)
	{
		int v=_data.itemPriceDic.get(id);
		
		if(v==0)
		{
			v=ItemConfig.get(id).tradeDefaultPrice;
		}
		
		return v;
	}
	
	/** 从中心服读取数据 */
	public void readFromCenter(GameAuctionToolData data)
	{
		_data=data;
	}
	
	/** 刷新物品价格 */
	public void refreshItemPrice(IntIntMap dic)
	{
		_data.itemPriceDic=dic;
	}
}
