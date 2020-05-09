package com.home.commonBase.tool.func;

import com.home.commonBase.config.game.ItemConfig;
import com.home.commonBase.constlist.generate.CallWayType;
import com.home.commonBase.constlist.generate.FuncToolType;
import com.home.commonBase.constlist.generate.ItemType;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.data.item.ItemDicContainerData;
import com.home.commonBase.data.item.UseItemArgData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.VBoolean;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.support.collection.inter.IObjectConsumer;
import com.home.shine.utils.ObjectUtils;

import java.util.Comparator;

/** 物品列表容器工具(无格背包) */
public class ItemDicContainerTool extends BaseItemContainerTool
{
	/** 数据 */
	protected ItemDicContainerData _data;
	/** 物品组(key:index) */
	protected IntObjectMap<ItemData> _dic;
	/** 物品组(key:itemID,key2:index) */
	protected IntObjectMap<IntObjectMap<ItemData>> _dicByID=new IntObjectMap<>(IntObjectMap[]::new);
	
	/** 物品数目组 */
	private IntIntMap _itemNums=new IntIntMap();
	
	//temp
	
	/** 临时物品移除组 */
	private SList<ItemData> _tempRemoveItemList=new SList<>(ItemData[]::new);
	/** 移除比较器 */
	private Comparator<ItemData> _removeComparator;
	
	public ItemDicContainerTool(int funcID)
	{
		super(FuncToolType.ItemDicContainer,funcID);
		
		_removeComparator=this::compareItemForRemove;
	}
	
	@Override
	protected void toSetData(FuncToolData data)
	{
		super.toSetData(data);
		_data=(ItemDicContainerData)data;
		
		_dic=_data.items;
		//_gridNum=_data.gridNum;
		_gridNum=0;
		//TODO:之后实现完整gridNum
	}
	
	@Override
	public ItemDicContainerData getData()
	{
		return _data;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_dic=null;
		_gridNum=0;
		_dicByID.clear();
		_itemNums.clear();
	}
	
	@Override
	public void afterReadData()
	{
		super.afterReadData();
		
		reMakeData();
	}
	
	@Override
	public void forEachItem(IObjectConsumer<ItemData> consumer)
	{
		ItemData[] values;
		ItemData v;

		for(int i=(values=_dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				consumer.accept(v);
			}
		}
	}
	
	/** 重新构造辅助数据 */
	private void reMakeData()
	{
		_itemNums.clear();
		_dicByID.clear();
		
		_dic.forEach((k,v) ->
		{
			//绑定index
			v.index=k;
			v.reloadConfig();
			
			getItemDicByIDAbs(v.id).put(v.index,v);
			
			_itemNums.addValue(v.id,v.num);
		});
	}
	
	@Override
	protected FuncToolData createToolData()
	{
		return new ItemDicContainerData();
	}
	
	@Override
	public void onNewCreate()
	{
		super.onNewCreate();
		
		_data.items=new IntObjectMap<>(ItemData[]::new);
		_data.serverItemIndex=0;
		_data.clientItemIndex=ShineSetting.indexMaxHalf;
		
		toSetData(_data);
		reMakeData();
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(!_itemTimeSet.isEmpty())
		{
			long now=getTimeEntity().getTimeMillis();
			
			VBoolean has=new VBoolean();
			
			_itemTimeSet.forEachS(k->
			{
				ItemData data=getItem(k);
				
				//超时
				if(now>=data.disableTime)
				{
					doRemoveItemByIndexC(k,data);
					has.value=true;
				}
			});
			
			if(has.value)
			{
				flushRemove(CallWayType.ItemTimeOut);
			}
		}
	}
	
	/** 获取物品组(id为key)(没有就创建) */
	private IntObjectMap<ItemData> getItemDicByIDAbs(int id)
	{
		IntObjectMap<ItemData> tDic=_dicByID.get(id);
		
		if(tDic==null)
		{
			_dicByID.put(id,tDic=new IntObjectMap<>(ItemData[]::new));
		}
		
		return tDic;
	}
	
	//方法组
	
	/** 添加物品到指定空位置(正序)(以及空闲格子计算) */
	private void addItemToPos(int index,ItemData data)
	{
		//先扩容再检测
		if(ShineSetting.openCheck)
		{
			if(_dic.get(index)!=null)
			{
				Ctrl.throwError("不该已有物品");
			}
		}
		
		data.index=index;//标记index
		_dic.put(index,data);
		
		getItemDicByIDAbs(data.id).put(index,data);
	}
	
	/** 移除指定序号的物品(倒序)(以及空闲格子计算) */
	private void removeItemFromPos(int index,ItemData data)
	{
		_dic.remove(index);
		getItemDicByIDAbs(data.id).remove(index);
	}
	
	/** 获取物品 */
	@Override
	public ItemData getItem(int index)
	{
		return _dic.get(index);
	}
	
	/** 获取某ID的第一个物品 */
	@Override
	public ItemData getItemByID(int id)
	{
		IntObjectMap<ItemData> dic=_dicByID.get(id);
		
		if(dic==null)
			return null;
		
		if(!dic.isEmpty())
		{
			return dic.getEver();
		}
		
		return null;
	}
	
	@Override
	public int getGridUseNum()
	{
		return _dic.size();
	}
	
	protected int justGetNewIndex()
	{
		if(CommonSetting.isClient)
		{
			int index=++_data.clientItemIndex;
			
			if(index>=ShineSetting.indexMax)
				index=ShineSetting.indexMaxHalf+1;
			
			return index;
		}
		else
		{
			int index=++_data.serverItemIndex;
			
			if(index>=ShineSetting.indexMaxHalf)
				index=1;
			
			return index;
		}
	}
	
	/** 获取一个新物品序号 */
	private int getNewIndex()
	{
		int index;
		
		while(_dic.contains(index=justGetNewIndex()));
		
		return index;
	}
	
	/** 判断是否有单个物品位置(核心) */
	@Override
	protected boolean doHasItemPlaceC(int id,int num,ItemData data)
	{
		ItemConfig config;
		
		if(data!=null)
		{
			data.makeConfig();
			config=data.config;
		}
		else
		{
			config=ItemConfig.get(id);
		}
		
		//自动使用道具
		if(config.type==ItemType.Tool && config.passEnterBag)
			return true;
		
		int totalNum=_itemNums.get(id);
		
		//超出总上限
		if(config.totalPlusMax>0 && (totalNum+num)>config.totalPlusMax)
			return false;
		
		//无上限
		if(_gridNum<=0)
			return true;
		
		//超单个上限的
		if(config.singlePlusMax>0 && num>config.singlePlusMax)
		{
			int needGridNum=(int)Math.ceil((double)num/config.singlePlusMax);
			
			return needGridNum<=doGetFreeGridNum();
		}
		
		//有空余格子
		if(!isGridFull())
			return true;
		
		if(config.singlePlusMax==1 || !config.enableTimeT.isEmpty())
			return false;
		
		IntObjectMap<ItemData> dic=_dicByID.get(id);
		
		if(dic==null)
			return false;
		
		if(config.singlePlusMax<=0)
			return true;
		
		//是否绑定
		boolean isBind=config.bindByGet || (data!=null && data.isBind);
		
		ItemData[] values;
		ItemData v;
		
		for(int i=(values=dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.isBind==isBind)
				{
					num-=(config.singlePlusMax-v.num);
					
					if(num<=0)
						return true;
				}
			}
		}
		
		return false;
	}
	
	/** 执行添加一个物品(核心) */
	@Override
	protected boolean doAddItemC(int id,int num,ItemData data)
	{
		ItemConfig config;
		
		if(data!=null)
		{
			data.makeConfig();
			config=data.config;
		}
		else
		{
			config=ItemConfig.get(id);
		}
		
		if(config.type==ItemType.Tool && config.passEnterBag)
		{
			_operateRecordAddListForAutoUse.add2(id,num);
			return true;
		}
		
		int totalNum=_itemNums.get(id);
		
		//超出总上限
		if(config.totalPlusMax>0 && (totalNum+num)>config.totalPlusMax)
			return false;
		
		//是否单个叠加
		boolean isSingleOne=config.singlePlusMax==1;
		//是否绑定
		boolean isBind=config.bindByGet || (data!=null && data.isBind);
		//失效时间
		long disableTime=data!=null ? data.disableTime : config.enableTimeT.getNextTime(getTimeEntity());
		
		int itemPos;
		int index=-1;
		int tempIndex;
		ItemData tData;
		int dNum;
		
		IntObjectMap<ItemData> itemDic=getItemDicByIDAbs(id);
		
		//非单个
		if(!isSingleOne)
		{
			ItemData[] values;
			ItemData v;
			
			for(int i=(values=itemDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//满足叠加规则
					if(!v.isSingleNumMax() && v.isBind==isBind && v.disableTime==disableTime && (data==null || canPlusEx(v,data)))
					{
						//装的下
						if(config.singlePlusMax<=0 || (dNum=(config.singlePlusMax-v.num))>=num)
						{
							dNum=num;
							num=0;
						}
						else
						{
							num-=dNum;
						}
						
						v.num+=dNum;//计数加
						
						//序号
						index=v.index;
						
						//操作记录
						_operateRecordList.add2(index,dNum);
						
						doAddItemPartial(index,dNum,v);
						
						//完了
						if(num==0)
						{
							return true;
						}
					}
				}
			}
		}
		
		if(num>0)
		{
			while(true)
			{
				//单包上限都超的
				if(config.singlePlusMax>0 && num>config.singlePlusMax)
				{
					dNum=config.singlePlusMax;
					num-=dNum;
				}
				else
				{
					dNum=num;
					num=0;
				}
				
				//最后的一次加,或者新添加,并且单个叠加的才可直接使用原数据
				if(data!=null && num==0 && isSingleOne)
				{
					//clone
					tData=(ItemData)data.clone();
					tData.config=config;
					tData.num=dNum;//数目再赋值
				}
				else
				{
					tData=createItemByType(config.type);
					tData.id=id;
					tData.num=dNum;
					tData.config=config;
					tData.canRelease=true;//标记可回收
					//额外初始化
					BaseC.logic.initItem(tData,id);
				}
				
				//获得新序号
				index=tData.index=getNewIndex();
				
				//赋值
				tData.isBind=isBind;
				tData.disableTime=disableTime;
				
				_operateRecordList.add2(index,dNum);
				doAddItemNew(index,dNum,tData);
				
				//完了
				if(num==0)
				{
					return true;
				}
			}
		}
		
		return true;
	}
	
	/** 添加道具(新格子)(对应remove的completely) */
	@Override
	protected void doAddItemNew(int index,int num,ItemData data)
	{
		addItemToPos(index,data);
		
		_itemNums.addValue(data.id,num);
		
		if(!data.config.enableTimeT.isEmpty())
		{
			_itemTimeSet.add(index);
		}
	}
	
	@Override
	protected void doAddItemPartial(int index,int num,ItemData data)
	{
		_itemNums.addValue(data.id,num);
	}
	
	/** 执行移除一个物品(倒序)(核心) */
	@Override
	protected boolean doRemoveItemC(int id,int num)
	{
		int totalNum=_itemNums.get(id);
		
		if(totalNum<num)
			return false;
		
		IntObjectMap<ItemData> iDic=_dicByID.get(id);
		
		if(iDic==null)
		{
			Ctrl.errorLog("此处不该为空");
			return false;
		}
		
		//只有一个
		if(iDic.size()==1)
		{
			ItemData data=iDic.getEver();
			
			if((num=doRemoveItemOneC(data,num))==0)
			{
				return true;
			}
		}
		else
		{
			_tempRemoveItemList.clear();
			
			ItemData[] values;
			ItemData v;
			
			for(int i=(values=iDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_tempRemoveItemList.add(v);
				}
			}
			
			//排序
			_tempRemoveItemList.sort(_removeComparator);
			
			ItemData[] values1=_tempRemoveItemList.getValues();
			ItemData v1;
			
			for(int i1=0,len1=_tempRemoveItemList.size();i1<len1;++i1)
			{
				v1=values1[i1];
				
				num=doRemoveItemOneC(v1,num);
				
				if(num==0)
				{
					_tempRemoveItemList.clear();
					return true;
				}
			}
			
			_tempRemoveItemList.clear();
		}
		
		Ctrl.errorLog("物品不该没移除完");
		
		return false;
	}
	
	private int doRemoveItemOneC(ItemData data,int num)
	{
		int dNum;
		
		//还有剩余
		if(data.num>num)
		{
			dNum=num;
			data.num-=num;
			num=0;
			
			_operateRecordList.add2(data.index,dNum);
			
			doRemoveItemPartial(data.index,dNum,data.id);
		}
		else
		{
			dNum=data.num;
			num-=data.num;
			//data.num=0;
			
			_operateRecordList.add2(data.index,dNum);
			_operateRecordRemoveDic.put(data.index,data);
			data.canRelease=true;//回收标记
			
			doRemoveItemCompletely(data.index,dNum,data);
		}
		
		return num;
	}
	
	@Override
	protected void doRemoveItemByIndexC(int index,int num,ItemData data)
	{
		//完全移除
		if(num==data.num)
		{
			_operateRecordList.add2(index,num);
			_operateRecordRemoveDic.put(index,data);
			doRemoveItemCompletely(index,num,data);
		}
		//部分移除
		else
		{
			data.num-=num;
			
			_operateRecordList.add2(index,num);
			_itemNums.addValue(data.id,-num);
		}
	}
	
	@Override
	protected void doRemoveItemPartial(int index,int num,int id)
	{
		_itemNums.addValue(id,-num);
	}
	
	/** 完全移除一个格子(对应add的new) */
	@Override
	protected void doRemoveItemCompletely(int index,int num,ItemData data)
	{
		_itemNums.addValue(data.id,-num);
		
		removeItemFromPos(index,data);
		
		//失效时间
		if(!data.config.enableTimeT.isEmpty())
		{
			_itemTimeSet.remove(index);
		}
	}
	
	//接口组
	
	/** 获取某道具的总数目 */
	@Override
	public int getItemNum(int itemID)
	{
		return _itemNums.get(itemID);
	}
	
	/** 通过ID使用物品 */
	@Override
	public boolean useItemByID(int id,UseItemArgData arg)
	{
		if(getItemNum(id)==0)
			return false;
		
		IntObjectMap<ItemData> dic=_dicByID.get(id);
		
		ItemData[] values;
		ItemData v;
		
		for(int i=(values=dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				return doUseItem(v,v.index,1,arg);
			}
		}
		
		return false;
	}
	
	/** 打印背包 */
	@Override
	public void printBag()
	{
		if(isEmpty())
		{
			Ctrl.log("bag is empty");
			return;
		}
		
		ObjectUtils.printDataDic(_dic);
	}
	
	@Override
	protected void updateItem(int index,ItemData data)
	{
		ItemData oldData=_dic.get(index);
		
		_dic.put(index,data);
		
		if(oldData!=null && oldData.id!=data.id)
		{
			getItemDicByIDAbs(oldData.id).remove(index);
		}
		
		getItemDicByIDAbs(data.id).put(index,data);
	}
	
	/** 比较物品,整理1用 */
	protected int compareItemForRemove(ItemData a1,ItemData a2)
	{
		return Integer.compare(a1.index,a2.index);
	}
}
