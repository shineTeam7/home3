using System;
using ShineEngine;

/// <summary>
/// 物品字典容器
/// </summary>
public class ItemDicContainerTool:BaseItemContainerTool
{
	/** 数据 */
	private ItemDicContainerData _data;
	/** 物品组(key:index) */
	protected IntObjectMap<ItemData> _dic;
	/** 物品组(key:itemID,key2:index) */
	protected IntObjectMap<IntObjectMap<ItemData>> _dicByID=new IntObjectMap<IntObjectMap<ItemData>>();

	/** 物品数目组 */
	private IntIntMap _itemNums=new IntIntMap();

	/** 物品显示列表 */
	private SList<ItemData> _showList=new SList<ItemData>();
	/** 显示列表是否变更 */
	private bool _showListDirty=true;

	//temp

	/** 临时物品移除组 */
	private SList<ItemData> _tempRemoveItemList=new SList<ItemData>();

	public ItemDicContainerTool(int funcID):base(FuncToolType.ItemDicContainer,funcID)
	{

	}

	protected override void toSetData(FuncToolData data)
	{
		base.toSetData(data);
		_data=(ItemDicContainerData)data;

		_showListDirty = true;

		_data=(ItemDicContainerData)data;

		_dic=_data.items;
		//_gridNum=_data.gridNum;
		_gridNum=0;
		//TODO:之后实现完整gridNum
	}

	public override void afterReadData()
	{
		base.afterReadData();

		_dic=_data.items;

		reMakeData();
	}

	public override void forEachItem(Action<ItemData> consumer)
	{
		ItemData[] values;
		ItemData v;

		for(int i=(values=_dic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				consumer(v);
			}
		}
	}

	/** 重新构造辅助数据 */
	private void reMakeData()
	{
		_itemNums.clear();
		_dicByID.clear();

		foreach(var kv in _dic.entrySet())
		{
			ItemData v=kv.value;
			//绑定index
			v.index=kv.key;
			v.reloadConfig();

			getItemDicByIDAbs(v.id).put(v.index,v);

			_itemNums.addValue(v.id,v.num);
		}

	}

	protected override FuncToolData createToolData()
	{
		return new ItemDicContainerData();
	}

	public override void onNewCreate()
	{
		base.onNewCreate();

		_data.items=new IntObjectMap<ItemData>();
		_data.serverItemIndex=0;
		_data.clientItemIndex=ShineSetting.indexMaxHalf;

		toSetData(_data);
		reMakeData();
	}

	public override void onSecond(int delay)
	{
		base.onSecond(delay);

		if(!_itemTimeSet.isEmpty())
		{
			long now=DateControl.getTimeMillis();

			bool has=false;

			ItemData data;

			foreach(int k in _itemTimeSet)
			{
				data=getItem(k);

				//超时
				if(now>=data.disableTime)
				{
					doRemoveItemByIndexC(k,data);
					has=true;
				}
			}

			if(has)
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
			_dicByID.put(id,tDic=new IntObjectMap<ItemData>());
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

		_showListDirty=true;
	}

	/** 移除指定序号的物品(倒序)(以及空闲格子计算) */
	private void removeItemFromPos(int index,ItemData data)
	{
		_dic.remove(index);
		getItemDicByIDAbs(data.id).remove(index);

		_showListDirty=true;
	}

	/** 获取物品 */
	public override ItemData getItem(int index)
	{
		return _dic.get(index);
	}

	/** 获取某ID的第一个物品 */
	public override ItemData getItemByID(int id)
	{
		IntObjectMap<ItemData> dic=_dicByID.get(id);

		if(dic==null)
			return null;

		if(!dic.isEmpty())
		{
			ItemData[] values;
			ItemData v;

			for(int i=(values=dic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					return v;
				}
			}
		}

		return null;
	}

	public override int getGridUseNum()
	{
		return _dic.size();
	}

	private int justGetNewIndex()
	{
		int index=++_data.clientItemIndex;

		if(index>=ShineSetting.indexMax)
			index=ShineSetting.indexMaxHalf+1;

		return index;
	}

	/** 获取一个新物品序号 */
	private int getNewIndex()
	{
		int index;

		while(_dic.contains(index=justGetNewIndex()));

		return index;
	}

	/** 判断是否有单个物品位置(核心) */
	protected override bool doHasItemPlaceC(int id,int num,ItemData data)
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

		return true;
	}

	/** 执行添加一个物品(核心) */
	protected override bool doAddItemC(int id,int num,ItemData data)
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
		bool isSingleOne=config.singlePlusMax==1;
		//是否绑定
		bool isBind=config.bindByGet || (data!=null && data.isBind);
		//失效时间
		long disableTime=data!=null ? data.disableTime : config.enableTimeT.getNextTime();

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

			for(int i=(values=itemDic.getValues()).Length-1;i>=0;--i)
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
					tData=data;
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
	protected override void doAddItemNew(int index,int num,ItemData data)
	{
		addItemToPos(index,data);

		_itemNums.addValue(data.id,num);

		if(!data.config.enableTimeT.isEmpty())
		{
			_itemTimeSet.add(index);
		}
	}

	protected override void doAddItemPartial(int index,int num,ItemData data)
	{
		_itemNums.addValue(data.id,num);
	}

	/** 执行移除一个物品(倒序)(核心) */
	protected override bool doRemoveItemC(int id,int num)
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

			for(int i=(values=iDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_tempRemoveItemList.add(v);
				}
			}

			//排序
			_tempRemoveItemList.sort(compareItemForRemove);

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

	protected override void doRemoveItemByIndexC(int index,int num,ItemData data)
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

	protected override void doRemoveItemPartial(int index,int num,int id)
	{
		_itemNums.addValue(id,-num);
	}

	/** 完全移除一个格子(对应add的new) */
	protected override void doRemoveItemCompletely(int index,int num,ItemData data)
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
	public override int getItemNum(int itemID)
	{
		return _itemNums.get(itemID);
	}

	/** 通过ID使用物品 */
	public override bool useItemByID(int id,UseItemArgData arg)
	{
		if(getItemNum(id)==0)
			return false;

		IntObjectMap<ItemData> dic=_dicByID.get(id);

		ItemData[] values;
		ItemData v;

		for(int i=(values=dic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				return doUseItem(v,v.index,1,arg);
			}
		}

		return false;
	}

	/** 打印背包 */
	public override void printBag()
	{
		if(isEmpty())
		{
			Ctrl.log("bag is empty");
			return;
		}

		ObjectUtils.printDataDic(_dic);
	}

	protected override void updateItem(int index,ItemData data)
	{
		ItemData oldData=_dic.get(index);

		_dic.put(index,data);

		if(oldData!=null && oldData.id!=data.id)
		{
			getItemDicByIDAbs(oldData.id).remove(index);
		}
		
		getItemDicByIDAbs(data.id).put(index,data);

		_showListDirty = true;
	}

	/** 获取显示列表 */
	public override SList<ItemData> getShowList()
	{
		if(_showListDirty)
		{
			_showListDirty=false;
			toCountShowList();
		}

		return _showList;
	}

	protected void toCountShowList()
	{
		_showList.clear();

		ItemData[] values;
		ItemData v;

		for(int i=(values=_dic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				_showList.add(v);
			}
		}

		_showList.sort(compareItem);
	}

	/** 比较物品,整理1用 */
	protected int compareItemForRemove(ItemData a1,ItemData a2)
	{
		return MathUtils.intCompare(a1.index,a2.index);
	}
}