package com.home.commonGame.tool.func;

import com.home.commonBase.config.game.AuctionConfig;
import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.config.game.enumT.AuctionQueryConditionTypeConfig;
import com.home.commonBase.constlist.generate.AuctionQueryConditionType;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.CurrencyType;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.constlist.generate.InfoLogType;
import com.home.commonBase.constlist.generate.QueryConditionCompareType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.auction.AuctionBuyItemData;
import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.AuctionQueryConditionData;
import com.home.commonBase.data.item.auction.AuctionSoldLogData;
import com.home.commonBase.data.item.auction.IntAuctionQueryConditionData;
import com.home.commonBase.data.item.auction.PlayerAuctionToolData;
import com.home.commonBase.data.role.RoleSimpleShowData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.table.table.AuctionItemTable;
import com.home.commonBase.utils.BaseGameUtils;
import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.request.func.auction.FuncAuctionAddSaleItemRequest;
import com.home.commonGame.net.request.func.auction.FuncAuctionReQueryRequest;
import com.home.commonGame.net.request.func.auction.FuncAuctionRefreshSaleItemRequest;
import com.home.commonGame.net.request.func.auction.FuncAuctionRemoveSaleItemRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionBuyItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionCancelSellItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.center.func.auction.FuncSendAuctionSellItemToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionBuyItemToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionCancelSellItemToSourceGameServerRequest;
import com.home.commonGame.net.serverRequest.game.func.auction.FuncSendAuctionSellItemToSourceGameServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.pool.StringBuilderPool;
import com.home.shine.table.BaseTable;
import com.home.shine.utils.TimeUtils;

/** 玩家身上拍卖行工具 */
public class PlayerAuctionTool extends PlayerFuncTool
{
	private PlayerAuctionToolData _data;
	
	private AuctionConfig _config;
	
	/** 是否是中心服拍卖行 */
	private boolean _isCenter;
	
	/** 货币类型 */
	private int _coinType=CurrencyType.Coin;
	
	/** 查询结果组 */
	private LongObjectMap<AuctionItemData> _queryResultDic=new LongObjectMap<>(AuctionItemData[]::new);
	
	private AuctionItemTable _tempTable;
	
	private BytesReadStream _tempReadStream;
	
	public PlayerAuctionTool(int funcID,int auctionID)
	{
		super(FuncToolType.Auction,funcID);
		
		_config=AuctionConfig.get(auctionID);
	}
	
	@Override
	public void construct()
	{
		super.construct();
	}
	
	@Override
	public void init()
	{
		super.init();
		
		_isCenter=(GameC.global.func.getAuctionToolBase(_funcID) instanceof GameToCenterAuctionTool);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_queryResultDic.clear();
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_data=(PlayerAuctionToolData)data;
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
	}
	
	@Override
	public void beforeLoginOnMain()
	{
		super.beforeLoginOnMain();
		
		checkSellTimeOut();
	}
	
	public PlayerAuctionToolData getData()
	{
		return _data;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return BaseC.factory.createPlayerAuctionToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(!_data.preSellItems.isEmpty())
		{
			AuctionItemData[] values;
			AuctionItemData v;
			
			for(int i=(values=_data.preSellItems.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(--v.tempTimeIndex<=0)
					{
						v.tempTimeIndex=ShineSetting.affairDefaultExecuteTime;
						doSendOnePreSell(v);
					}
				}
			}
		}
		
		if(!_data.preBuyItems.isEmpty())
		{
			AuctionBuyItemData[] values;
			AuctionBuyItemData v;
			
			for(int i=(values=_data.preBuyItems.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(--v.tempTimeIndex<=0)
					{
						v.tempTimeIndex=ShineSetting.affairDefaultExecuteTime;
						doSendOnePreBuy(v);
					}
				}
			}
		}
	}
	
	@Override
	public void onMinute()
	{
		super.onMinute();
		
		checkSellTimeOut();
	}
	
	private void checkSellTimeOut()
	{
		if(!_data.sellItems.isEmpty())
		{
			long removeTime=me.getTimeMillis()-(_config.keepDay* TimeUtils.dayTime);
			
			AuctionItemData[] values;
			AuctionItemData v;
			
			for(int i=(values=_data.sellItems.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.sellTime<removeTime)
					{
						doCancelSellItem(v,InfoCodeType.Auction_removeSellReason_timeOut);
					}
				}
			}
		}
	}
	
	/** 上架物品 */
	public void sellItem(int index,int itemID,int num,int price)
	{
		ItemData item=doCheckSell(index,itemID,num);
		
		if(item==null)
			return;
		
		ItemConfig config=ItemConfig.get(item.id);
		
		//价格不对
		if(price<config.tradePriceMin || price>config.tradePriceMax)
		{
			me.warningInfoCode(InfoCodeType.Auction_sellItem_wrongPrice,price);
			return;
		}
		
		long playerID=me.role.playerID;
		int funcID=_funcID;
		
		me.addMainFunc(()->
		{
			long instanceID=GameC.global.func.getAuctionToolBase(funcID).getItemInstanceID();
			
			Player player=GameC.main.getPlayerByID(playerID);
			
			if(player!=null)
			{
				player.addFunc(()->
				{
					PlayerAuctionTool tool=player.func.getAuctionTool(funcID);
					
					tool.sellItemNext(index,itemID,num,price,instanceID);
				});
			}
		});
	}
	
	private ItemData doCheckSell(int index,int itemID,int num)
	{
		//数不够
		if(_config.maxSaleNum>0 && getNowSaleNum()>=_config.maxSaleNum)
		{
			me.warningInfoCode(InfoCodeType.Auction_sellItem_isMaxSaleNum);
			return null;
		}
		
		ItemData item=me.bag.getItem(index);
		
		if(item==null)
		{
			me.warningInfoCode(InfoCodeType.Auction_sellItem_cantFindItem,index);
			return null;
		}
		
		if(item.id!=itemID)
		{
			me.warningInfoCode(InfoCodeType.Auction_sellItem_wrongItemID,index);
			return null;
		}
		
		//num非法
		if(num<=0 || num>item.num)
		{
			me.warningInfoCode(InfoCodeType.Auction_sellItem_wrongNum,num);
			return null;
		}
		
		return item;
	}
	
	private void sellItemNext(int index,int itemID,int num,int price,long instanceID)
	{
		ItemData item=doCheckSell(index,itemID,num);
		
		ItemData cloneItem=(ItemData)item.clone();
		cloneItem.num=num;

		AuctionItemData aData=BaseC.factory.createAuctionItemData();
		aData.data=cloneItem;
		aData.price=price;
		aData.instanceID=instanceID;
		aData.playerID=me.role.playerID;
		aData.sellTime=me.getTimeMillis();
		
		me.bag.removeItemByIndex(index,num,CallWayType.AuctionSellItem);
		
		aData.tempTimeIndex=ShineSetting.affairDefaultExecuteTime;
		_data.preSellItems.put(instanceID,aData);
		
		doSendOnePreSell(aData);
	}
	
	private void doSendOnePreSell(AuctionItemData data)
	{
		if(_isCenter)
		{
			FuncSendAuctionSellItemToCenterServerRequest.create(me.role.playerID,_funcID,data).send();
		}
		else
		{
			if(me.isCurrentGame())
			{
				GameC.global.func.getAuctionTool(_funcID).addExecutorFunc(data.instanceID,executor->
				{
					executor.sellItem(data);
				});
			}
			else
			{
				FuncSendAuctionSellItemToSourceGameServerRequest.create(_funcID,data).send(me.role.sourceGameID);
			}
		}
	}
	
	/** 获取当前上架数目 */
	private int getNowSaleNum()
	{
		return _data.sellItems.size()+_data.preSellItems.size();
	}
	
	/** 出售物品结果 */
	public void onSellItemResult(long instanceID,boolean success)
	{
		AuctionItemData preItem=_data.preSellItems.remove(instanceID);
		
		if(preItem==null)
		{
			me.warnLog("拍卖行收到出售物品结果的时候，找不到preItem",instanceID,success);
			return;
		}
		
		//成功
		if(success)
		{
			_data.sellItems.put(instanceID,preItem);
			
			me.send(FuncAuctionAddSaleItemRequest.create(_funcID,preItem));
		}
		//失败
		else
		{
			me.bag.addItemAbs(preItem.data,CallWayType.AuctionSellItemFailed);
		}
	}
	
	/** 取消上架物品 */
	public void cancelSellItem(long instanceID)
	{
		AuctionItemData data=_data.sellItems.get(instanceID);
		
		if(data==null)
		{
			if(_data.preSellItems.get(instanceID)!=null)
			{
				me.warningInfoCode(InfoCodeType.Auction_cancelSellItem_stillSaleOn,instanceID);
			}
			else
			{
				me.warningInfoCode(InfoCodeType.Auction_cancelSellItem_notExist,instanceID);
			}
			
			return;
		}
		
		doCancelSellItem(data,InfoCodeType.Auction_removeSellReason_cancelInitiative);
	}
	
	private void doCancelSellItem(AuctionItemData data,int reason)
	{
		if(_isCenter)
		{
			FuncSendAuctionCancelSellItemToCenterServerRequest.create(me.role.playerID,_funcID,data.instanceID,reason).send();
		}
		else
		{
			if(me.isCurrentGame())
			{
				GameC.global.func.getAuctionTool(_funcID).addExecutorFunc(data.instanceID,executor->
				{
					executor.removeSellItem(data.playerID,data.instanceID,reason);
				});
			}
			else
			{
				FuncSendAuctionCancelSellItemToSourceGameServerRequest.create(_funcID,me.role.playerID,data.instanceID,reason).send(me.role.sourceGameID);
			}
		}
	}
	
	/** 查询拍卖行数据 */
	public void query(AuctionQueryConditionData[] conditions,int page)
	{
		if(conditions.length==0)
		{
			me.warningInfoCode(InfoCodeType.Auction_query_conditionIsEmpty);
			return;
		}
		
		if(_tempTable==null)
			_tempTable=BaseC.factory.createAuctionItemTable();
		
		AuctionItemTable table=_tempTable;
		
		AuctionQueryConditionData con;
		boolean hasWhere=false;
		boolean hasOrder=false;
		
		StringBuilder sb=StringBuilderPool.create();
		sb.append(table.getSelectStr());
		
		int whereIndex=sb.length();
		int n=0;
		
		for(int i=0;i<conditions.length;i++)
		{
			AuctionQueryConditionTypeConfig config=AuctionQueryConditionTypeConfig.get((con=conditions[i]).type);
			
			//不是排序
			if(config.compareType!=QueryConditionCompareType.Sort)
			{
				hasWhere=true;
				
				if(n>0)
				{
					sb.append(' ');
					sb.append("and");
				}
				
				writeQueryCondition(sb,config,con);
				
				n++;
			}
			else
			{
				hasOrder=true;
			}
		}
		
		if(hasWhere)
		{
			sb.insert(whereIndex," where");
		}
		
		if(hasOrder)
		{
			sb.append(" order by ");
		}
		
		n=0;
		
		for(int i=0;i<conditions.length;i++)
		{
			AuctionQueryConditionTypeConfig config=AuctionQueryConditionTypeConfig.get((con=conditions[i]).type);
			
			if(config.compareType==QueryConditionCompareType.Sort)
			{
				if(n>0)
				{
					sb.append(' ');
					sb.append(",");
				}
				
				writeQueryCondition(sb,config,con);
				
				n++;
			}
		}
		
		sb.append(" limit ");
		sb.append(_config.eachPageShowNum*page);
		sb.append(',');
		sb.append(_config.eachPageShowNum);
		
		//不加结束符
		//sb.append(';');
		
		String sql=StringBuilderPool.releaseStr(sb);
		
		Ctrl.print(sql);
		
		long playerID=me.role.playerID;
		int funcID=_funcID;
		LogicExecutor executor=me.system.getExecutor();
		
		table.loadCustomSql(GameC.db.getCenterConnect(),sql,list->
		{
			Player player=executor.getPlayer(playerID);
			
			//已不在
			if(player==null)
			{
				me.warningInfoCode(InfoCodeType.Auction_query_playerNotExist);
				return;
			}
			
			PlayerAuctionTool tool=player.func.getAuctionTool(funcID);
			
			tool.onReceiveQuery(list,page);
		});
	}
	
	/** 查询某物品(测试用) */
	public void queryItem(int itemID,int page)
	{
		IntAuctionQueryConditionData con=new IntAuctionQueryConditionData();
		con.type=AuctionQueryConditionType.ItemID;
		con.min=itemID;
		
		query(new AuctionQueryConditionData[]{con},page);
	}
	
	/** 查询最近(测试用) */
	public void queryRecently(int page)
	{
		query(new AuctionQueryConditionData[]{AuctionQueryConditionData.create(AuctionQueryConditionType.Recently)
				,AuctionQueryConditionData.create(AuctionQueryConditionType.RecentlySort)},page);
	}
	
	/** 接收查询结果 */
	protected void onReceiveQuery(SList<BaseTable> list,int page)
	{
		_queryResultDic.clear();
		
		if(_tempReadStream==null)
			_tempReadStream=BytesReadStream.create();
		
		SList<AuctionItemData> reList=new SList<>(AuctionItemData[]::new);
		
		SList<BaseTable> fDic;
		if(!(fDic=list).isEmpty())
		{
			BaseTable[] values=fDic.getValues();
			AuctionItemTable v;
			
			for(int i=0,len=fDic.size();i<len;++i)
			{
				v=(AuctionItemTable)values[i];
				
				_tempReadStream.setBuf(v.data);
				ItemData item=(ItemData)_tempReadStream.readDataFullNotNull();
				item.num=v.itemNum;
				
				AuctionItemData aData=BaseC.factory.createAuctionItemData();
				aData.instanceID=v.instanceID;
				aData.playerID=v.playerID;
				aData.data=item;
				aData.price=v.price;
				aData.sellTime=v.sellTime;
				
				readAuctionItemTable(v,aData);
				
				reList.add(aData);
				
				_queryResultDic.put(aData.instanceID,aData);
			}
		}
		
		me.send(FuncAuctionReQueryRequest.create(_funcID,page,reList));
	}
	
	/** 写入查询条件 */
	protected void writeQueryCondition(StringBuilder sb,AuctionQueryConditionTypeConfig config,AuctionQueryConditionData condition)
	{
		switch(condition.type)
		{
			case AuctionQueryConditionType.Recently:
			{
				long time=me.getTimeMillis() - _config.recentlyTime * 1000;
				
				sb.append(' ');
				sb.append('`');
				sb.append(getFieldName(condition.type));
				sb.append('`');
				sb.append(' ');
				sb.append("> ");
				sb.append(time);
			}
				break;
			default:
			{
				//不是排序
				if(config.compareType!=QueryConditionCompareType.Sort)
				{
					sb.append(' ');
					sb.append('`');
					sb.append(getFieldName(condition.type));
					sb.append('`');
					sb.append(' ');
					
					IntAuctionQueryConditionData iCon=(IntAuctionQueryConditionData)condition;
					
					switch(config.compareType)
					{
						case QueryConditionCompareType.Equals:
						{
							sb.append("= ");
							sb.append(iCon.min);
						}
						break;
						case QueryConditionCompareType.Between:
						{
							sb.append("between ");
							sb.append(iCon.min);
							sb.append(" and ");
							sb.append(iCon.max);
						}
						break;
						case QueryConditionCompareType.Less:
						{
							sb.append("< ");
							sb.append(iCon.min);
						}
						break;
						case QueryConditionCompareType.More:
						{
							sb.append("> ");
							sb.append(iCon.min);
						}
						break;
					}
					
				}
				else
				{
					sb.append(' ');
					sb.append('`');
					sb.append(getFieldName(condition.type));
					sb.append('`');
					sb.append(' ');
					
					if(config.isDesc)
					{
						sb.append("DESC");
					}
				}
			}
		}
	}
	
	protected String getFieldName(int type)
	{
		switch(type)
		{
			case AuctionQueryConditionType.ItemID:
				return "itemID";
			case AuctionQueryConditionType.ItemLevel:
				return "itemLevel";
			case AuctionQueryConditionType.ItemType:
				return "itemType";
			case AuctionQueryConditionType.ItemSecondType:
				return "itemSecondType";
			case AuctionQueryConditionType.Recently:
				return "sellTime";
			case AuctionQueryConditionType.RecentlySort:
				return "sellTime";
		}
		
		return "";
	}
	
	/** 读取拍卖行物品表 */
	protected void readAuctionItemTable(AuctionItemTable table,AuctionItemData data)
	{
	
	}
	
	/** 接收移除上架物品 */
	public void onRemoveSellItem(long instanceID,int lastNum,int code)
	{
		AuctionItemData data=_data.sellItems.remove(instanceID);
		
		if(data==null)
		{
			me.warnLog("拍卖行收到取消出售物品结果的时候，找不到preItem",instanceID);
			return;
		}
		
		//没了
		if(lastNum==0)
		{
			me.warnLog("接收拍卖行取消物品上架时,不应该出现,物品为0的时候");
		}
		else
		{
			ItemData item=data.data;
			item.num=lastNum;
			
			me.bag.addItemAbs(item,CallWayType.AuctionCancelSellItem);
			
			me.send(FuncAuctionRemoveSaleItemRequest.create(_funcID,instanceID,code));
		}
	}
	
	/** 购买物品 */
	public void buyItem(AuctionBuyItemData data)
	{
		if(_data.preBuyItems.contains(data.instanceID))
		{
			me.warningInfoCode(InfoCodeType.Auction_buyItem_isBuying);
			return;
		}
		
		ItemConfig config=ItemConfig.get(data.itemID);
		
		if(config==null)
		{
			me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongItemID);
			return;
		}
		
		if(data.price<config.tradePriceMin || data.price>config.tradePriceMax)
		{
			me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongPrice,data.price);
			return;
		}
		
		if(!BaseGameUtils.checkClientNum(data.num))
		{
			me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongNum,data.price);
			return;
		}
		
		AuctionItemData qData=_queryResultDic.get(data.instanceID);
		
		//有上次查询结果
		if(qData!=null)
		{
			//价格不对
			if(qData.data.id!=data.itemID)
			{
				me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongItemID);
				return;
			}
			
			//数目不对
			if(qData.data.num<data.num)
			{
				me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongNum);
				return;
			}
			
			//价格不对
			if(qData.price!=data.price)
			{
				me.warningInfoCode(InfoCodeType.Auction_buyItem_wrongPrice);
				return;
			}
			
			//不可购买自己的
			if(qData.playerID==me.role.playerID)
			{
				me.warningInfoCode(InfoCodeType.Auction_buyItem_cantBuySelf);
				return;
			}
		}
		
		int m=data.price*data.num;
		
		if(!me.role.hasCurrency(_coinType,m))
		{
			me.warningInfoCode(InfoCodeType.Auction_buyItem_coinNotEnough,data.price);
			return;
		}
		
		me.role.costCurrency(_coinType,m,CallWayType.AuctionBuyItem);
		
		_data.preBuyItems.put(data.instanceID,data);
		data.tempTimeIndex=ShineSetting.affairDefaultExecuteTime;
		doSendOnePreBuy(data);
	}
	
	private void doSendOnePreBuy(AuctionBuyItemData data)
	{
		RoleSimpleShowData roleSimpleShowData=me.role.createRoleSimpleShowData();
		
		if(_isCenter)
		{
			FuncSendAuctionBuyItemToCenterServerRequest.create(me.role.playerID,_funcID,roleSimpleShowData,data).send();
		}
		else
		{
			if(me.isCurrentGame())
			{
				GameC.global.func.getAuctionTool(_funcID).addExecutorFunc(data.instanceID,executor->
				{
					executor.buyItem(roleSimpleShowData,data);
				});
			}
			else
			{
				FuncSendAuctionBuyItemToSourceGameServerRequest.create(_funcID,roleSimpleShowData,data).send(me.role.sourceGameID);
			}
		}
	}
	
	/** 购买物品 */
	public void onBuyItem(long instanceID,ItemData data,int code)
	{
		AuctionBuyItemData bData=_data.preBuyItems.remove(instanceID);
		
		if(bData==null)
		{
			me.warnLog("拍卖行收到取消购买物品结果的时候，找不到preItem",instanceID);
			return;
		}
		
		//失败
		if(data==null)
		{
			me.role.addCurrencyAbs(_coinType,bData.num*bData.price,CallWayType.AuctionBuyItemFailed);
			me.sendInfoCode(code);
		}
		//成功
		else
		{
			addBoughtItem(data);
		}
	}
	
	/** 添加购买的物品 */
	protected void addBoughtItem(ItemData data)
	{
		me.bag.addItemAbs(data,CallWayType.AuctionBuyItem);
	}
	
	/** 出售了物品 */
	public void onSoldItem(long instanceID,int num,RoleSimpleShowData consumer,boolean isSoldOut)
	{
		AuctionItemData bData=_data.sellItems.get(instanceID);
		
		if(bData==null)
		{
			me.warnLog("拍卖行收到物品被购买结果的时候，找不到item",instanceID);
			return;
		}
		
		bData.data.num-=num;
		
		if(isSoldOut)
		{
			if(bData.data.num!=0)
			{
				me.errorLog("出现错误，售罄时，数目不为0");
				bData.data.num=0;
			}
		}
		
		long coin=num*bData.price;
		
		//手续费
		if(_config.commissionRatio>0)
		{
			coin=Math.round(coin*_config.commissionRatioT);
		}
		
		me.role.addCurrencyAbs(_coinType,coin,CallWayType.AuctionSoldItem);
		
		AuctionSoldLogData logData=new AuctionSoldLogData();
		logData.funcID=_funcID;
		logData.id=InfoLogType.AuctionSoldItem;
		logData.item=(ItemData)bData.data.clone();
		logData.role=consumer;
		logData.price=bData.price;
		
		me.addInfoLog(logData);
		
		if(isSoldOut)
		{
			_data.sellItems.remove(instanceID);
			me.send(FuncAuctionRemoveSaleItemRequest.create(_funcID,instanceID,InfoCodeType.Auction_removeSellReason_soldOut));
		}
		else
		{
			me.send(FuncAuctionRefreshSaleItemRequest.create(_funcID,instanceID,bData.data.num));
		}
	}
}
