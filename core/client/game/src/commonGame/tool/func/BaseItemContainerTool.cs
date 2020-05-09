using System;
using ShineEngine;

/// <summary>
/// 基础物品容器工具
/// </summary>
public abstract class BaseItemContainerTool:PlayerFuncTool
{
	/** 格子数(0为无上限) */
	protected int _gridNum;
	/** 是否是背包容器 */
	protected bool _isBag;

	/** 有过期时间的物品组(key:index) */
	protected IntSet _itemTimeSet=new IntSet();
	
	//temp
	/** 操作记录组(index:dNum) */
	protected IntList _operateRecordList=new IntList(8);
	/** 操作记录组(id:num) */
	protected IntList _operateRecordAddListForAutoUse=new IntList(8);
	/** 操作移除记录字典(为rollBackRemove) */
	protected IntObjectMap<ItemData> _operateRecordRemoveDic=new IntObjectMap<ItemData>();
	
	protected IntIntMap _tempDic=new IntIntMap();
	
	/** 格子序号改变组 */
	protected SList<ItemData> _tempAddItems=new SList<ItemData>();

	/** 是否正在处理中 */
	private bool _flushing=false;

	/** 红点总数 */
	protected int _redPointCount=0;


	public BaseItemContainerTool(int type,int funcID):base(type,funcID)
	{
		
	}

	/** 是否是背包容器 */
	public void setIsBag(bool value)
	{
		_isBag=value;
	}
	
	public override void onReloadConfig()
	{
		forEachItem(v=>v.reloadConfig());
	}
	
	/** 遍历当前物品 */
	abstract public void forEachItem(Action<ItemData> consumer);
	
	/** 获取物品 */
	abstract public ItemData getItem(int index);
	
	/** 获取某ID的第一个物品 */
	abstract public ItemData getItemByID(int id);

	/** 获取已使用格子数 */
	abstract public int getGridUseNum();
	
	/** 背包是否为空 */
	public bool isEmpty()
	{
		return getGridUseNum()==0;
	}

	/** 是否格子已满 */
	public bool isGridFull()
	{
		return _gridNum>0 && getGridUseNum()>=_gridNum;
	}

	/** 获取空闲格子数目(无限返回-1) */
	public int getFreeGridNum()
	{
		return _gridNum>0 ? _gridNum-getGridUseNum() : -1;
	}

	protected int doGetFreeGridNum()
	{
		return _gridNum-getGridUseNum();
	}
	
 	/** 获取物品总数 */
	abstract public int getItemNum(int itemID);

	/** 是否有空余格子 */
	public bool hasFreeGrid(int num)
	{
		if(_gridNum<=0)
			return true;

		return num<=(_gridNum-getGridUseNum());
	}

	/** 某序号是否可用(在0<=index<gridNum) */
	public virtual bool isIndexEnable(int index)
	{
		return true;
	}
	
	/** 是否可叠加 */
	protected bool canPlus(ItemData oldItem,ItemData newItem)
	{
		return oldItem.id==newItem.id && oldItem.isBind==newItem.isBind && oldItem.disableTime==newItem.disableTime && canPlusEx(oldItem,newItem);
	}
	
	/** 是否可叠加(额外判定部分) */
	protected bool canPlusEx(ItemData oldItem,ItemData newItem)
	{
		return true;
	}

	/** 获取红点数目 */
	public int getRedPointCount()
	{
		return _redPointCount;
	}
	
	/** 移除执行序号物品数目(核心)(不回收) */
	protected void doRemoveItemByIndexC(int index,ItemData data)
	{
		doRemoveItemByIndexC(index,data.num,data);
	}
	
	/** 移除执行序号物品数目(核心)(不回收) */
	abstract protected void doRemoveItemByIndexC(int index,int num,ItemData data);
	
	public override void onSecond(int delay)
	{
		base.onSecond(delay);

		if(CommonSetting.isClientDriveLogic && !_itemTimeSet.isEmpty())
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
	
	/** 结算添加结果 */
	protected void flushAdd(int way)
	{
		if(_flushing)
		{
			Ctrl.throwError("物品操作出现环");
			return;
		}

		_flushing=true;

		try
		{
			IntIntMap autoUseItems=null;

			if(!_operateRecordAddListForAutoUse.isEmpty())
			{
				autoUseItems=new IntIntMap();

				int[] values=_operateRecordAddListForAutoUse.getValues();

				int len=_operateRecordAddListForAutoUse.size();

				for(int i=0;i<len;i+=2)
				{
					// toUseItem(values[i],values[i+1],null);

					autoUseItems.addValue(values[i],values[i + 1]);
				}

				_operateRecordAddListForAutoUse.clear();
			}

			IntObjectMap<ItemData> dic=null;

			if(!_operateRecordList.isEmpty())
			{
				int[] values=_operateRecordList.getValues();

				int index;
				int num;

				//单个,并且没有自动使用
				if(_operateRecordList.size()==2) // && autoUseItems==null
				{
					index=values[0];
					num=values[1];
					ItemData v=getItem(index);
					v.canRelease=false;
					v.index=index; //标记index

					// //新增物品
					// if(v.num==num)
					// {
					// 	toSendAddOneItem(index,v,way);
					// }
					// else
					// {
					// 	toSendAddOneItemNum(index,v.num,way);
					// }

					onItemAdd(index,v,num,way);
				}
				else
				{
					dic=new IntObjectMap<ItemData>();

					ItemData v;

					int len=_operateRecordList.size();

					for(int i=0;i<len;i+=2)
					{
						index=values[i];
						num=values[i + 1];
						v=getItem(index);

						v.canRelease=false; //清除回收标记
						v.index=index; //标记index

						dic.put(index,v);

						onItemAdd(index,v,num,way);
					}

					//推送下面
				}

				_operateRecordList.clear();

				onChanged();
			}

			// if(autoUseItems!=null || dic!=null)
			// {
			// 	toSendAddItem(autoUseItems,dic,way);
			// }

			if(autoUseItems!=null)
			{
				autoUseItems.forEach((k,v)=>{ toUseItem(k,v,null); });
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}


		_flushing=false;
	}
	
	/** 结算移除结果 */
	protected void flushRemove(int way)
	{
		if(_operateRecordList.isEmpty())
			return;

		if(_flushing)
		{
			Ctrl.throwError("物品操作出现环");
			return;
		}

		_flushing=true;

		try
		{
			int[] values=_operateRecordList.getValues();

			int index;
			int num;
			ItemData data;
			ItemData oldData;

			//单个
			if(_operateRecordList.size()==2)
			{
				data=getItem(index=_operateRecordList.get(0));
				num=values[1];

				// toSendRemoveOneItem(index,data!=null ? data.num : 0,way);

				oldData=data!=null ? data : _operateRecordRemoveDic.get(index);

				onItemRemove(index,oldData,num,way);
			}
			else
			{
				// IntIntMap dic=new IntIntMap();

				int len=_operateRecordList.size();

				for(int i=0;i<len;i+=2)
				{
					data=getItem(index=values[i]);
					num=values[i+1];

					// dic.put(index,data!=null ? data.num : 0);

					oldData=data!=null ? data : _operateRecordRemoveDic.get(index);

					onItemRemove(index,oldData,num,way);
				}

				// toSendRemoveItem(dic,way);
			}

			_operateRecordList.clear();

			if(!_operateRecordRemoveDic.isEmpty())
			{
				ItemData[] values2;
				ItemData v;

				for(int i=(values2=_operateRecordRemoveDic.getValues()).Length-1;i>=0;--i)
				{
					if((v=values2[i])!=null)
					{
						if(v.canRelease)
						{
							releaseItem(v);
						}
					}
				}

				_operateRecordRemoveDic.clear();
			}

			onChanged();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}

		_flushing=false;
	}
	
	/** 添加操作回滚 */
	protected void rollBackAdd()
	{
		_operateRecordAddListForAutoUse.clear();
		
		if(!_operateRecordList.isEmpty())
		{
			int[] values=_operateRecordList.getValues();
			int index;
			int num;
			
			ItemData data;
			
			for(int i=_operateRecordList.size()-2;i>=0;i-=2)
			{
				index=values[i];
				num=values[i+1];
				
				data=getItem(index);
				
				//移除
				if((data.num-=num)<=0)
				{
					doRemoveItemCompletely(index,num,data);
					
					//可回收
					if(data.canRelease)
					{
						releaseItem(data);
					}
				}
				else
				{
					doRemoveItemPartial(index,num,data.id);
				}
			}
			
			_operateRecordList.clear();
		}
	}
	
	/** 移除操作回滚 */
	protected void rollBackRemove()
	{
		if(_operateRecordList.isEmpty())
			return;
		
		int[] values=_operateRecordList.getValues();
		int index;
		int num;
		
		ItemData data;
		ItemData removeData;
		
		for(int i=_operateRecordList.size()-2;i>=0;i-=2)
		{
			index=values[i];
			num=values[i+1];
			
			data=getItem(index);
			
			//被移除了
			if(data==null)
			{
				removeData=_operateRecordRemoveDic.get(index);
				removeData.num=num;//数目恢复
				//重新添加
				doAddItemNew(index,num,removeData);
			}
			else
			{
				doAddItemPartial(index,num,data);
			}
		}
		
		_operateRecordList.clear();
		_operateRecordRemoveDic.clear();
	}
	
	/** 创建物品 */
	protected virtual ItemData createItemByType(int type)
	{
		ItemData re=GameC.factory.createItemData();
		re.initIdentityByType(type);
		return re;
	}
	
	/** 析构物品 */
	protected virtual void releaseItem(ItemData data)
	{
	
	}
	
	//c组
	
	/** 判断是否有单个物品位置(核心) */
	abstract protected bool doHasItemPlaceC(int id,int num,ItemData data);
	
	/** 执行添加一个物品(核心) */
	abstract protected bool doAddItemC(int id,int num,ItemData data);
	
	/** 执行移除一个物品(倒序)(核心) */
	abstract protected bool doRemoveItemC(int id,int num);
	
	/** 添加道具(新格子)(对应remove的completely) */
	abstract protected void doAddItemNew(int index,int num,ItemData data);
	
	/** 添加道具部分 */
	abstract protected void doAddItemPartial(int index,int num,ItemData data);
	
	/** 完全移除一个格子(对应add的new) */
	abstract protected void doRemoveItemCompletely(int index,int num,ItemData data);
	
	/** 部分移除一个格子 */
	abstract protected void doRemoveItemPartial(int index,int num,int id);
	
	//has
	
	/** 是否有物品位置 */
	public bool hasItemPlace(ItemData data)
	{
		return doHasItemPlaceC(data.id,data.num,data);
	}
	
	/** 是否有物品位置 */
	public bool hasItemPlace(int id,int num)
	{
		return doHasItemPlaceC(id,num,null);
	}
	
	/** 是否有物品位置 */
	public bool hasItemPlace(SList<ItemData> list)
	{
		if(list.size()==1)
		{
			return hasItemPlace(list.get(0));
		}
		
		_tempDic.clear();
		
		ItemData[] values=list.getValues();
		ItemData data;
		
		for(int i=0,len=list.size();i<len;++i)
		{
			data=values[i];
			
			//有必要
			if(ItemConfig.get(data.id).totalPlusMax>0)
			{
				_tempDic.put(data.id,data.num);
			}
		}
		
		if(!_tempDic.isEmpty())
		{
			ItemConfig config;

			foreach(var kv in _tempDic.entrySet())
			{
				config=ItemConfig.get(kv.key);

				//超出总上限
				if((getItemNum(kv.key)+kv.value)>config.totalPlusMax)
				{
					return false;
				}
			}
		}
		
		return hasFreeGrid(list.size());
	}
	
	/** 是否有物品位置 */
	public bool hasItemPlace(DIntData[] dataArr)
	{
		DIntData data;

		if(dataArr.Length==1)
		{
			data=dataArr[0];
			return hasItemPlace(data.key,data.value);
		}

		_tempDic.clear();

		for(int i=0,len=dataArr.Length;i<len;++i)
		{
			data=dataArr[i];

			//有必要
			if(ItemConfig.get(data.key).totalPlusMax>0)
			{
				_tempDic.put(data.key,data.value);
			}
		}

		if(!_tempDic.isEmpty())
		{
			ItemConfig config;

			foreach(var kv in _tempDic.entrySet())
			{
				config=ItemConfig.get(kv.key);

				//超出总上限
				if((getItemNum(kv.key)+kv.value)>config.totalPlusMax)
				{
					return false;
				}
			}
		}

		return hasFreeGrid(dataArr.Length);
	}
	
	
	//add
	
	/** 添加物品数据 */
	public bool addItem(ItemData data,int way)
	{
		if(!doAddItemC(data.id,data.num,data))
		{
			rollBackAdd();
			return false;
		}
		
		flushAdd(way);
		return true;
	}
	
	public bool addItem(int id,int way)
	{
		return addItem(id,1,way);
	}
	
	/** 添加指定id和数目的道具 */
	public bool addItem(int id,int num,int way)
	{
		if(!doAddItemC(id,num,null))
		{
			rollBackAdd();
			return false;
		}
		
		flushAdd(way);
		return true;
	}
	
	/** 添加一组物品 */
	public bool addItems(SList<ItemData> list,int way)
	{
		ItemData[] values=list.getValues();
		ItemData data;
		
		for(int i=0,len=list.size();i<len;i++)
		{
			data=values[i];
			
			if(!doAddItemC(data.id,data.num,data))
			{
				rollBackAdd();
				return false;
			}
		}
		
		flushAdd(way);
		return true;
	}
	
	/** 添加一组物品 */
	public bool addItems(DIntData[] list,int num,int way)
	{
		DIntData data;
		
		for(int i=0,len=list.Length;i<len;i++)
		{
			data=list[i];
			
			if(!doAddItemC(data.key,data.value*num,null))
			{
				rollBackAdd();
				return false;
			}
		}
		
		flushAdd(way);
		return true;
	}
	
	//contains
	
	/** 是否有指定id的物品 */
	public bool containsItem(int id)
	{
		return getItemNum(id)>0;
	}
	
	/** 是否有指定id数目的物品 */
	public bool containsItem(int id,int num)
	{
		return getItemNum(id)>=num;
	}
	
	//remove
	
	/** 移除道具(1个)(会回收) */
	public bool removeItem(int id,int way)
	{
		return removeItem(id,1,way);
	}
	
	/** 移除道具(会回收) */
	public bool removeItem(int id,int num,int way)
	{
		if(!doRemoveItemC(id,num))
		{
			rollBackRemove();
			return false;
		}
		
		flushRemove(way);
		return true;
	}

	/** 移除物品组 */
	public bool removeItems(DIntData[] items,int num,int way)
	{
		foreach(DIntData v in items)
		{
			if(!doRemoveItemC(v.key,v.value*num))
			{
				rollBackRemove();
				return false;
			}
		}

		flushRemove(way);
		return true;
	}
	
	/** 移除指定序号的物品(全部数目)(不回收) */
	public bool removeItemByIndex(int index,int way)
	{
		ItemData data=getItem(index);
		
		if(data==null)
			return false;
		
		doRemoveItemByIndexC(index,data);
		flushRemove(way);
		
		return true;
	}
	
	/** 移除指定序号的物品(部分数目)(不回收) */
	public bool removeItemByIndex(int index,int num,int way)
	{
		ItemData data=getItem(index);
		
		if(data==null)
			return false;
		
		if(data.num<num)
			return false;
		
		doRemoveItemByIndexC(index,num,data);
		flushRemove(way);
		
		return true;
	}

	/** 添加一个新物品到格子 */
	public bool addNewItemToIndex(int index,ItemData data,int way)
	{
		if(getItem(index)!=null)
			return false;

		data.makeConfig();

		_operateRecordList.add2(index,data.num);
		doAddItemNew(index,data.num,data);
		flushAdd(way);

		return true;
	}

	/** 给某格子物品添加一定数目 */
	public bool addItemPartialToIndex(int index,int num,int way)
	{
		ItemData item=getItem(index);

		if(item==null)
			return false;

		int plusLast=item.getPlusLast();

		if(plusLast!=-1 && plusLast<num)
			return false;

		//操作记录
		_operateRecordList.add2(index,num);
		item.num+=num;
		doAddItemPartial(index,num,item);
		flushAdd(way);

		return true;
	}

	//use
	
	/** 通过ID使用物品 */
	abstract public bool useItemByID(int id,UseItemArgData arg);
	
	/** 使用物品 */
	public bool useItemByIndex(int index,int num,UseItemArgData arg)
	{
		ItemData data;
		
		if((data=getItem(index))==null)
			return false;
		
		return doUseItem(data,index,num,arg);
	}
	
	/** 客户端使用物品 */
	public bool clientUseItemByIndex(int index,int num,int itemID,UseItemArgData arg)
	{
		ItemData data;
		
		if((data=getItem(index))==null)
			return false;
		
		if(data.id!=itemID)
			return false;
		
		return doUseItem(data,index,num,arg);
	}
	
	protected bool doUseItem(ItemData data,int index,int num,UseItemArgData arg)
	{
		//不是道具不能使用
		if(data.config.type!=ItemType.Tool)
			return false;
		
		if(data.num<num)
			return false;
		
		if(!checkCanUseItem(data,num,arg,true))
			return false;
		
		if(CommonSetting.isClientDriveLogic)
		{
			bool needRemove=data.num==num;
			
			doRemoveItemByIndexC(index,num,data);
			
			flushRemove(CallWayType.UseItem);
			toUseItem(data,num,arg);

			if(needRemove)
			{
				//回收
				releaseItem(data);
			}
		}
		else
		{
			toSendUseItem(data,index,num,arg);
		}
		
		return true;
	}
	
	/** 打印背包 */
	abstract public void printBag();
	
	//接口
	
	/** 单格物品添加 */
	protected virtual void onItemAdd(int index,ItemData data,int num,int way)
	{
		//添加红点
		if(CommonSetting.isClientDriveLogic && data.config.needRedPoint)
		{
			data.hasRedPoint=true;
		}

		//加红点
		if(data.config.needRedPoint && !data.hasRedPoint)
		{
			data.hasRedPoint=true;
			_redPointCount++;

			onRedPointChange();
		}

		onItemNumChanged(data.id);
	}
	
	/** 单格物品减少 */
	protected virtual void onItemRemove(int index,ItemData data,int num,int way)
	{
		//减红点
		if(data.hasRedPoint)
		{
			data.hasRedPoint=false;
			_redPointCount--;

			onRedPointChange();
		}

		onItemNumChanged(data.id);
	}

	/** 物品数目改变 */
	protected virtual void onItemNumChanged(int id)
	{

	}
	
	/** 背包改变 */
	protected virtual void onChanged()
	{
	
	}

	/** 红点被移除 */
	protected virtual void onRedPointRemoved(int index)
	{

	}

	/** 红点改变 */
	protected virtual void onRedPointChange()
	{

	}

	/** 全刷(格子数增加/整理) */
	protected virtual void onRefreshAll()
	{
		onChanged();
	}
	
	//client
	
	/** 更新物品 */
	abstract protected void updateItem(int index,ItemData data);
	
	/** 执行添加物品(来自服务器) */
	private void doAddItemByServer(int index,ItemData data,int way)
	{
		data.makeConfig();
		
		//先把来自服务器的标记取消,由客户端自己加
		data.hasRedPoint=false;
		
		ItemData oldData=getItem(index);
		
		int dNum=0;
		
		if(oldData==null)
		{
			doAddItemNew(index,dNum=data.num,data);
		}
		else
		{
			data.index=index;
			updateItem(index,data);
			
			doAddItemPartial(index,dNum=data.num - oldData.num,data);
			
			//TODO:oldData回收
		}
		
		onItemAdd(index,data,dNum,way);
		
		if(CallWayConfig.get(way).needAddItemNotice)
		{
			ItemData temp=(ItemData)data.clone();
			temp.num=dNum;
			_tempAddItems.add(temp);
		}
	}
	
	/** 执行添加物品(来自服务器) */
	private void doRemoveItemByServer(int index,int num,int way)
	{
		ItemData oldData=getItem(index);
		
		if(oldData==null)
		{
			Ctrl.warnLog("收到移除物品时，找不到物品",_funcID,index);
			return;
		}
		
		int dNum=oldData.num-num;
		
		oldData.num=num;
		
		//移除
		if(oldData.num==0)
		{
			doRemoveItemCompletely(index,dNum,oldData);
		}
		else
		{
			doRemoveItemPartial(index,dNum,oldData.id);
		}
		
		onItemRemove(index,oldData,dNum,way);
	}
	
	/** 服务器添加物品 */
	public void onAddItemByServer(int index,ItemData data,int way)
	{
		_tempAddItems.clear();
		
		doAddItemByServer(index,data,way);
		
		onChanged();
		
		if(CallWayConfig.get(way).needAddItemNotice)
		{
			onAddItemNotice(_tempAddItems,way);
			_tempAddItems.clear();
		}
	}
	
	/** 服务器添加物品 */
	public void onAddItemNumByServer(int index,int num,int way)
	{
		ItemData data=getItem(index);
		
		if(data==null)
		{
			Ctrl.warnLog("服务器添加物品数目时,找不到物品",_funcID,index);
			return;
		}
		
		int dNum=num - data.num;
		
		data.num=num;
		
		doAddItemPartial(index,dNum,data);
		
		onItemAdd(index,data,dNum,way);
		
		onChanged();
		
		if(CallWayConfig.get(way).needAddItemNotice)
		{
			_tempAddItems.clear();
			
			ItemData temp=(ItemData)data.clone();
			temp.num=dNum;
			_tempAddItems.add(temp);
			onAddItemNotice(_tempAddItems,way);
			_tempAddItems.clear();
		}
	}
	
	/** 服务器添加物品 */
	public void onAddItemsByServer(IntIntMap autoUseItems,IntObjectMap<ItemData> dic,int way)
	{
		bool need=CallWayConfig.get(way).needAddItemNotice;
		
		if(need)
		{
			_tempAddItems.clear();
			
			if(autoUseItems!=null)
			{
				autoUseItems.forEach((k,v)=>
				{
					_tempAddItems.add(GameC.player.bag.createItem(k,v));
				});
			}
		}
		
		if(dic!=null)
		{
			foreach(var kv in dic.entrySet())
			{
				doAddItemByServer(kv.key,kv.value,way);
			}
		}
		
		onChanged();
		
		if(CallWayConfig.get(way).needAddItemNotice)
		{
			//TODO:合并因为到达单个物品上限的拆分问题
			onAddItemNotice(_tempAddItems,way);
			_tempAddItems.clear();
		}
	}
	
	public void onRemoveItemByServer(int index,int num,int way)
	{
		doRemoveItemByServer(index,num,way);
	}
	
	public void onRemoveItemsByServer(IntIntMap dic,int way)
	{
		dic.forEach((k,v)=>
		{
			doRemoveItemByServer(k,v,way);
		});
	}
	
	public void onUseItemResult(int itemID,int num,bool result)
	{
		doUseItemResult(itemID,num,result);
	}

	/** 移除某序号物品红点 */
	public void removeRedPoint(int index)
	{
		ItemData data=getItem(index);

		if(data==null)
		{
			Ctrl.warnLog("不该找不到物品");
			return;
		}

		//减红点
		if(data.hasRedPoint)
		{
			data.hasRedPoint=false;
			_redPointCount--;

			onRedPointRemoved(index);

			onRedPointChange();
		}
	}

	/** 获得物品提示 */
	protected virtual void onAddItemNotice(SList<ItemData> list,int way)
	{
	
	}
	
	
	//send
	
	protected virtual void toSendAddOneItem(int index,ItemData data,int way)
	{
	
	}
	
	protected virtual void toSendAddOneItemNum(int index,int num,int way)
	{
	
	}
	
	protected virtual void toSendAddItem(IntIntMap autoUseItems,IntObjectMap<ItemData> dic,int way)
	{
	
	}
	
	protected virtual void toSendRemoveOneItem(int index,int num,int way)
	{
	
	}
	
	protected virtual void toSendRemoveItem(IntIntMap dic,int way)
	{
	
	}
	
	/** 发送使用物品 */
	protected virtual void toSendUseItem(ItemData data,int index,int num,UseItemArgData arg)
	{
	
	}
	
	/** 执行使用物品 */
	protected virtual void toUseItem(ItemData data,int num,UseItemArgData arg)
	{
	
	}
	
	/** 执行使用物品通过id */
	protected virtual void toUseItem(int id,int num,UseItemArgData arg)
	{
	
	}
	
	/** 检查是否可使用物品 */
	public virtual bool checkCanUseItem(ItemData data,int num,UseItemArgData arg,bool needNotice)
	{
		return true;
	}
	
	
	/** 服务器返回使用物品结果 */
	protected virtual void doUseItemResult(int id,int num,bool result)
	{
	
	}

	/** 比较物品 */
	protected int compareItem(ItemData a1,ItemData a2)
	{
		if(a2==null)
			return -1;

		if(a1==null)
			return 1;

		if(a2.num==0)
			return -1;

		if(a1.num==0)
			return 1;

		int temp1;
		int temp2;

		//先比序
		if((temp1=a1.config.sortIndex)<(temp2=a2.config.sortIndex))
			return -1;

		if(temp1>temp2)
			return 1;

		//再比id
		if((temp1=a1.id)<(temp2=a2.id))
			return -1;

		if(temp1>temp2)
			return 1;

		if(a1.isBind!=a2.isBind)
		{
			//不绑定的在前
			return !a1.isBind ? -1 : 1;
		}

		if(a1.disableTime!=a2.disableTime)
		{
			//时间长的在前
			return a1.disableTime>a2.disableTime ? -1 : 1;
		}

		//不可堆叠
		if(!canPlusEx(a1,a2))
		{
			return a1.num==a2.num ? 0 : (a1.num>a2.num ? -1 : 1);
		}

		return 0;
	}

	//client

	/** 获取显示列表 */
	abstract public SList<ItemData> getShowList();
}