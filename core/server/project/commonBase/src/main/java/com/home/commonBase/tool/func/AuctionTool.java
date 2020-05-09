package com.home.commonBase.tool.func;

import com.home.commonBase.config.game.AuctionConfig;
import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.InfoCodeType;
import com.home.commonBase.control.AbstractLogicExecutor;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.auction.AuctionBuyItemData;
import com.home.commonBase.data.item.auction.AuctionItemData;
import com.home.commonBase.data.item.auction.AuctionItemRecordData;
import com.home.commonBase.data.item.auction.AuctionReBuyItemOWData;
import com.home.commonBase.data.item.auction.AuctionReSellItemOWData;
import com.home.commonBase.data.item.auction.AuctionRemoveSellItemOWData;
import com.home.commonBase.data.item.auction.AuctionSoldItemOWData;
import com.home.commonBase.data.item.auction.AuctionToolData;
import com.home.commonBase.data.item.auction.GameAuctionToolData;
import com.home.commonBase.data.role.RoleSimpleShowData;
import com.home.commonBase.data.system.CountData;
import com.home.commonBase.data.system.PlayerWorkData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.table.table.AuctionItemTable;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.SList;
import com.home.shine.table.BaseTable;
import com.home.shine.table.DBConnect;
import com.home.shine.tool.TableOperateTool;
import com.home.shine.utils.TimeUtils;

/** 拍卖插件 */
public abstract class AuctionTool extends FuncTool
{
	private AuctionToolData _data;
	
	private AuctionConfig _config;
	
	private AuctionExecutor[] _executors;
	
	public AuctionTool(int funcID,int auctionID)
	{
		super(FuncToolType.Auction,funcID);
		
		_config=AuctionConfig.get(auctionID);
	}
	
	@Override
	public void construct()
	{
		super.construct();
		
		_executors=new AuctionExecutor[ShineSetting.poolThreadNum];
		
		for(int i=0;i<_executors.length;++i)
		{
			AuctionExecutor executor=new AuctionExecutor(i);
			_executors[i]=executor;
			
			ThreadControl.addPoolFunc(i,()->
			{
				executor.init();
			});
		}
	}
	
	protected abstract DBConnect getConnect();
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		
		_data=(AuctionToolData)data;
	}
	
	public AuctionToolData getData()
	{
		return _data;
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return new AuctionToolData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
	}
	
	@Override
	public void onDaily()
	{
		super.onDaily();
		
		if(!_data.itemRecords.isEmpty())
		{
			CountData cData;
			
			AuctionItemRecordData[] values;
			AuctionItemRecordData v;
			
			for(int i=(values=_data.itemRecords.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//先计算价格
					
					if(v.days.isEmpty())
					{
						Ctrl.errorLog("不可能出现days为空的时候");
						v.price=ItemConfig.get(v.id).tradeDefaultPrice;
					}
					else
					{
						v.price=countDaysPrice(v);
					}
					
					cData=new CountData();
					v.days.offer(cData);
					
					while(v.days.size()>_config.keepDay)
					{
						v.days.poll();
					}
				}
			}
			
			sendItemPriceChanged();
		}
		
		DBConnect connect=getConnect();
		AuctionItemTable table=BaseC.factory.createAuctionItemTable();
		table.loadAll(connect,list->
		{
			long removeTime=getTimeEntity().getTimeMillis()-_config.keepDay* TimeUtils.dayTime;
			
			SList<BaseTable> fDic;
			if(!(fDic=list).isEmpty())
			{
				BaseTable[] values=fDic.getValues();
				AuctionItemTable v;
				
				for(int i=0,len=fDic.size();i<len;++i)
				{
					v=(AuctionItemTable)values[i];
					
					//到时间
					if(v.sellTime<removeTime)
					{
						removeSellItemOnMain(v.playerID,v.instanceID,InfoCodeType.Auction_removeSellReason_timeOut);
					}
				}
			}
		});
	}
	
	protected void sendItemPriceChanged()
	{
	
	}
	
	private int countDaysPrice(AuctionItemRecordData data)
	{
		long re=0;
		int num=0;
		
		for(CountData v:data.days)
		{
			if(v.num>0)
			{
				re+=(v.total/v.num);
				num++;
			}
		}
		
		if(num>0)
		{
			re/=num;
		}
		
		if(re==0)
		{
			re=ItemConfig.get(data.id).tradeDefaultPrice;
		}
		
		return (int)re;
	}
	
	private void removeSellItemOnMain(long playerID,long instanceID,int reason)
	{
		//_executors[instanceID & ThreadControl.poolThreadNumMark]
	}
	
	/** 添加一次成功交易(主线程) */
	public void addOneSale(int id,int num,int price)
	{
		AuctionItemRecordData data=_data.itemRecords.get(id);
		
		CountData cData;
		
		if(data==null)
		{
			data=new AuctionItemRecordData();
			data.initDefault();
			data.id=id;
			data.price=ItemConfig.get(id).tradeDefaultPrice;
			
			cData=new CountData();
			data.days.offer(cData);
			
			_data.itemRecords.put(id,data);
		}
		
		CountData tail=data.days.tail();
		tail.num+=num;
		tail.total+=(price*num);
	}
	
	/** 获取物品建议价格 */
	public int getItemPrice(int id)
	{
		AuctionItemRecordData data=_data.itemRecords.get(id);
		
		if(data==null)
		{
			return ItemConfig.get(id).tradeDefaultPrice;
		}
		
		return data.price;
	}
	
	protected IntIntMap createItemPriceDic()
	{
		IntIntMap itemPriceDic=new IntIntMap();
		
		AuctionItemRecordData[] values;
		AuctionItemRecordData v;
		
		for(int i=(values=_data.itemRecords.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				itemPriceDic.put(v.id,v.price);
			}
		}
		
		return itemPriceDic;
	}
	
	public GameAuctionToolData createGameToolData()
	{
		GameAuctionToolData re=new GameAuctionToolData();
		re.initDefault();
		re.funcID=_funcID;
		re.itemPriceDic=createItemPriceDic();
		
		return re;
	}
	
	protected abstract void sendToPlayerWorkData(long playerID,PlayerWorkData data);
	
	/** 通过数据写拍卖行物品表 */
	protected void writeAuctionItemTable(AuctionItemTable table,AuctionItemData data)
	{
	
	}
	
	/** 获取执行器 */
	public AuctionExecutor getExecutor(int index)
	{
		return _executors[index];
	}
	
	/** 每个线程独立的执行器 */
	public class AuctionExecutor extends AbstractLogicExecutor
	{
		private TableOperateTool<Long,AuctionItemTable> _tableTool;
		
		private BytesReadStream _readStream=BytesReadStream.create();
		
		private BytesWriteStream _writeStream=BytesWriteStream.create();
		
		public AuctionExecutor(int index)
		{
			super(index);
		}
		
		@Override
		public void init()
		{
			super.init();
			
			_tableTool=new TableOperateTool<Long,AuctionItemTable>(getConnect())
			{
				@Override
				protected AuctionItemTable makeTable(Long key)
				{
					AuctionItemTable table=BaseC.factory.createAuctionItemTable();
					table.instanceID=key;
					return table;
				}
			};
		}
		
		/** 出售物品 */
		public void sellItem(AuctionItemData aData)
		{
			if(_tableTool.isDoing(aData.instanceID))
			{
				Ctrl.warnLog("收到出售物品时,正在执行",aData.toDataString());
				return;
			}
			
			AuctionItemTable table=BaseC.factory.createAuctionItemTable();
			table.instanceID=aData.instanceID;
			table.playerID=aData.playerID;
			_writeStream.clear();
			_writeStream.writeDataFullNotNull(aData.data);
			table.data=_writeStream.getByteArray();
			table.itemID=aData.data.id;
			table.itemLevel=ItemConfig.get(aData.data.id).itemLevel;
			table.itemNum=aData.data.num;
			table.price=aData.price;
			table.sellTime=aData.sellTime;
			
			writeAuctionItemTable(table,aData);
			
			_tableTool.insert(table.instanceID,table,t->
			{
				if(t!=null)
				{
					Ctrl.log("拍卖行物品上架:instanceID",t.instanceID,"playerID:",t.playerID,"itemID:",t.itemID,"num:",t.itemNum,"price:",t.price);
				}
				
				AuctionReSellItemOWData wData=new AuctionReSellItemOWData();
				wData.funcID=_funcID;
				wData.instanceID=aData.instanceID;
				wData.success=(t!=null);
				
				sendToPlayerWorkData(aData.playerID,wData);
				
			});
		}
		
		/** 取消上架物品 */
		public void removeSellItem(long playerID,long instanceID,int reason)
		{
			AuctionRemoveSellItemOWData wData=new AuctionRemoveSellItemOWData();
			wData.funcID=_funcID;
			wData.instanceID=instanceID;
			wData.code=reason;
			
			_tableTool.load(instanceID,t->
			{
				if(t!=null)
				{
					wData.lastNum=t.itemNum;
					//删除当前
					_tableTool.deleteCurrentNode(instanceID);
					
					Ctrl.log("拍卖行物品下架:",playerID,"itemID:",t.itemID,"num:",t.itemNum,"price:",t.price);
				}
				else
				{
					wData.lastNum=0;
				}
			},()->
			{
				sendToPlayerWorkData(playerID,wData);
			});
		}
		
		/** 购买物品 */
		public void buyItem(RoleSimpleShowData roleData,AuctionBuyItemData data)
		{
			AuctionReBuyItemOWData wData=new AuctionReBuyItemOWData();
			wData.funcID=_funcID;
			wData.instanceID=data.instanceID;
			
			_tableTool.load(data.instanceID,t->
			{
				if(t!=null)
				{
					if(t.price!=data.price)
					{
						wData.code=InfoCodeType.Auction_buyItem_wrongPrice;
						return;
					}
					
					if(t.itemNum<data.num)
					{
						wData.code=InfoCodeType.Auction_buyItem_numNotEnough;
						return;
					}
					
					if(t.playerID==roleData.playerID)
					{
						wData.code=InfoCodeType.Auction_buyItem_cantBuySelf;
						return;
					}
					
					_readStream.setBuf(t.data);
					wData.item=(ItemData)_readStream.readDataFullNotNull();
					wData.item.num=data.num;
					wData.code=InfoCodeType.Success;
					wData.tempPlayerID=t.playerID;
					
					t.itemNum-=data.num;
					
					Ctrl.log("拍卖行物品购买,购买者:",roleData.playerID,"itemID:",t.itemID,"num:",data.num,"price:",t.price);
					//TODO:服务器Log部分
					
					//卖完了
					if(t.itemNum<=0)
					{
						wData.tempSoldOut=true;
						_tableTool.deleteCurrentNode(data.instanceID);
					}
				}
				else
				{
					wData.code=InfoCodeType.Auction_buyItem_notExist;
				}
			},()->
			{
				//出售成功
				if(wData.item!=null)
				{
					long targetPlayerID=wData.tempPlayerID;
					
					AuctionSoldItemOWData swData=new AuctionSoldItemOWData();
					swData.instanceID=wData.instanceID;
					swData.consumer=roleData;
					swData.num=data.num;
					swData.isSoldOut=wData.tempSoldOut;
					
					sendToPlayerWorkData(targetPlayerID,swData);
					
					ThreadControl.addMainFunc(()->
					{
						addOneSale(data.itemID,data.num,data.price);
					});
				}
				
				sendToPlayerWorkData(roleData.playerID,wData);
			});
		}
	}
}
