using System;
using System.Collections.Generic;
using System.Text;
using C5;
using ShineEngine;
using UnityEngine;

/// <summary>
///
/// </summary>
public class ItemContainerTool:BaseItemContainerTool
{
	private ObjectPool<ItemRecordData> _recordDataPool=new ObjectPool<ItemRecordData>(()=>new ItemRecordData());

	/** 容器数据 */
	private ItemContainerData _data;
	/** 物品列表 */
	protected SList<ItemData> _list;

	/** 已使用格子数 */
	private int _useGridNum=0;
	/** 空闲格子数目(不需要则为-1) */
	private int _freeGridNum=-1;
	/** 下个自由格子索引 */
	private int _nextFreeGridIndex=-1;

	/** 物品记录字典(key:itemID) */
	private IntObjectMap<ItemRecordData> _itemRecordDic=new IntObjectMap<ItemRecordData>();

	/** 是否需要再次整理标记 */
	private bool _cleanUpDirty;

	/** 分钟计数 */
	private int _minuteCount=0;

	public ItemContainerTool(int funcID):base(FuncToolType.ItemContainer,funcID)
	{
		_recordDataPool.setEnable(CommonSetting.logicUsePool);
	}

	protected override void toSetData(FuncToolData data)
	{
		base.toSetData(data);
		_data=(ItemContainerData)data;

		_list=_data.items;
		_gridNum=_data.gridNum;
	}

	public override void afterReadData()
	{
		base.afterReadData();

		_list=_data.items;
		_gridNum=_data.gridNum;

		_cleanUpDirty=true;

		reMakeData();
	}

	public override void forEachItem(Action<ItemData> consumer)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		for(int i=0,len=_list.size();i<len;++i)
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
		IntSet itemTimeSet;

		if(!_itemRecordDic.isEmpty())
		{
			ItemRecordData[] values;
			ItemRecordData v;

			for(int i=(values=_itemRecordDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					_recordDataPool.back(v);
				}
			}

			_itemRecordDic.clear();
		}

		(itemTimeSet=_itemTimeSet).clear();

		_nextFreeGridIndex=-1;

		int len=0;

		SList<ItemData> list=_list;

		if(!list.isEmpty())
		{
			int index=list.size()-1;

			//先去末尾null
			while(index>=0 && list.get(index)==null)
			{
				--index;
			}

			list.justSetSize(index+1);


			ItemData[] values=list.getValues();
			ItemData data;

			len=list.size();

			ItemRecordData rData;

			for(int i=0;i<len;++i)
			{
				data=values[i];

				//空格子
				if(data==null)
				{
					if(_nextFreeGridIndex==-1)
						_nextFreeGridIndex=i;
				}
				else
				{
					++_useGridNum;

					//构造配置
					data.makeConfig();
					data.index=i;

					rData=getRecordDataAbs(data.id);
					rData.num+=data.num;
					rData.removeIndex=i;

					//不满
					if(!data.isSingleNumMax())
					{
						//没有就添加
						if(rData.addIndex!=-1)
						{
							rData.addIndex=i;
						}
					}

					if(!data.config.enableTimeT.isEmpty())
					{
						itemTimeSet.add(i);
					}

					//红点
					if(data.hasRedPoint)
					{
						_redPointCount++;
					}
				}
			}
		}

		if(_gridNum>0)
		{
			if(_gridNum-len>0)
			{
				if(_nextFreeGridIndex==-1)
					_nextFreeGridIndex=len;
			}
		}
		else
		{
			if(_nextFreeGridIndex==-1)
				_nextFreeGridIndex=len;
		}
	}

	private void clearRecordDatas()
	{
		IntObjectMap<ItemRecordData> itemRecordDic=_itemRecordDic;
		ObjectPool<ItemRecordData> pool=_recordDataPool;

		if(!itemRecordDic.isEmpty())
		{
			foreach(ItemRecordData v in itemRecordDic)
			{
				if(v.num==0)
				{
					itemRecordDic.remove(v.id);
					pool.back(v);
				}
			}
		}
	}

	private ItemRecordData getRecordData(int id)
	{
		return _itemRecordDic.get(id);
	}

	private ItemRecordData getRecordDataAbs(int id)
	{
		ItemRecordData re;

		if((re=_itemRecordDic.get(id))==null)
		{
			re=_recordDataPool.getOne();
			re.id=id;
			_itemRecordDic.put(id,re);
		}

		return re;
	}

	private void removeRecordData(ItemRecordData rData)
	{
		rData.clear();
	}

	/** 最大格子格子数 */
	public int getGridNum()
	{
		return _gridNum;
	}

	/** 某序号是否可用(在0<=index<gridNum) */
	public override bool isIndexEnable(int index)
	{
		return index>=0 && (_gridNum==0 || index<_gridNum);
	}

	/** 添加格子数 */
	public void setGridNum(int num)
	{
		if(num<=0)
		{
			warnLog("不可为设置为无上限");
			return;
		}

		if(_gridNum<=0)
		{
			warnLog("不可为无上限背包加格子数");
			return;
		}

		if(num==_gridNum)
			return;

		bool isMore=num>_gridNum;

		_data.gridNum=_gridNum=num;//格子数增加

		if(isMore)
		{
			if(_nextFreeGridIndex==-1)
			{
				_nextFreeGridIndex=_list.size();
			}
		}
		else
		{
			if(_nextFreeGridIndex>=_gridNum)
			{
				_nextFreeGridIndex=-1;
			}
		}

		onRefreshAll();
	}

	protected override FuncToolData createToolData()
	{
		return new ItemContainerData();
	}

	public override void onNewCreate()
	{
		base.onNewCreate();

		_data.items=new SList<ItemData>();
		_data.gridNum=0;//默认无上限

		toSetData(_data);
		reMakeData();
	}

	public override void onSecond(int delay)
	{
		base.onSecond(delay);

		if(++_minuteCount>=60)
		{
			_minuteCount=0;

			clearRecordDatas();
		}
	}

	//方法组

	/** 添加物品到指定空位置(正序)(以及空闲格子计算) */
	private void addItemToPos(int index,ItemData data)
	{
		_list.growSize(index+1);

		//先扩容再检测
		if(ShineSetting.openCheck)
		{
			if(_list.get(index)!=null)
			{
				Ctrl.throwError("不该已有物品");
			}
		}

		_list.set(index,data);
		data.index=index;

		++_useGridNum;

		toCountFreeGridByAdd(index);
	}

	private void toCountFreeGridByAdd(int index)
	{
		if(_nextFreeGridIndex!=-1 && index==_nextFreeGridIndex)
		{
			_nextFreeGridIndex=findNextFreeGridPos(index+1);
		}
	}

	private void toCountFreeGridByRemove(int index)
	{
		//新的空位
		if(index<_nextFreeGridIndex)
		{
			_nextFreeGridIndex=index;
		}

		//最后一个
		if(index==_list.size()-1)
		{
			while((--index)>=0 && _list.get(index)==null);
			//重设size
			_list.justSetSize(index+1);
		}
	}

	/** 移除指定序号的物品(倒序)(以及空闲格子计算) */
	private void removeItemFromPos(int index)
	{
		_list.set(index,null);

		--_useGridNum;

		toCountFreeGridByRemove(index);
	}
	
	/** 获取物品 */
	public override ItemData getItem(int index)
	{
		if(index>=_list.size())
			return null;

		return _list.get(index);
	}
	
	/** 获取某ID的第一个物品 */
	public override ItemData getItemByID(int id)
	{
		ItemRecordData rData;
		
		if((rData=getRecordData(id))!=null)
		{
			if(rData.num<=0 || rData.removeIndex==-1)
				return null;

			ItemData data;
			
			if((data=_list.get(rData.removeIndex))==null)
			{
				Ctrl.throwError("不该找不到数据");
				return null;
			}
			
			return data;
		}
		
		return null;
	}

	public override int getGridUseNum()
	{
		return _useGridNum;
	}

	/** 获取物品列表 */
	public SList<ItemData> getItemList()
	{
		return _list;
	}

	/** 找到下一个空余格子序号 */
	private int findNextFreeGridPos(int fromIndex)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		int len=_list.size();

		//找下一个不满的物品序号
		for(int i=fromIndex;i<len;++i)
		{
			if(values[i]==null)
			{
				return i;
			}
		}

		//无上限
		if(_gridNum<=0 || len<_gridNum)
		{
			return len;
		}

		return -1;
	}

	/** 找到下一个添加点 */
	private int findNextItemAddPos(int id,int fromIndex,bool isBind,long disableTime,ItemData data)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		//找下一个不满的物品序号
		for(int i=fromIndex,len=_list.size();i<len;++i)
		{
			//相同道具
			if((v=values[i])!=null && v.id==id)
			{
				//没满并且可叠加
				if(!v.isSingleNumMax() && v.isBind==isBind && v.disableTime==disableTime && (data==null || canPlusEx(v,data)))
				{
					return i;
				}
			}
		}

		return -1;
	}

	/** 计算下一个添加位置 */
	private int toCountNextItemAddPos(int id,int fromIndex)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		//找下一个不满的物品序号
		for(int i=fromIndex,len=_list.size();i<len;++i)
		{
			//相同道具
			if((v=values[i])!=null && v.id==id)
			{
				//没满并且可叠加
				if(!v.isSingleNumMax())
				{
					return i;
				}
			}
		}

		return -1;
	}

	/** 计算下一个添加位置 */
	private int toCountPrevItemAddPos(int id,int fromIndex)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		//找下一个不满的物品序号
		for(int i=fromIndex;i>=0;--i)
		{
			//相同道具
			if((v=values[i])!=null && v.id==id)
			{
				//没满并且可叠加
				if(!v.isSingleNumMax())
				{
					return i;
				}
			}
		}

		return -1;
	}

	/** 计算上一个物品移除位置 */
	private int toCountPrevItemRemovePos(int id,int fromIndex)
	{
		ItemData[] values=_list.getValues();
		ItemData v;

		//找下一个不满的物品序号
		for(int i=fromIndex;i>=0;--i)
		{
			//相同道具
			if((v=values[i])!=null && v.id==id)
			{
				return i;
			}
		}

		return -1;
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

		ItemRecordData rData=getRecordData(id);

		//自动使用道具
		if(config.type==ItemType.Tool && config.passEnterBag)
			return true;

		//超出总上限
		if(config.totalPlusMax>0 && ((rData!=null ? rData.num : 0 )+num)>config.totalPlusMax)
			return false;

		//无上限
		if(_gridNum<=0)
			return true;

		//超单个上限的
		if(config.singlePlusMax>0 && num>=config.singlePlusMax)
		{
			int needGridNum=(int)Mathf.Ceil((float)num/config.singlePlusMax);

			return needGridNum<=doGetFreeGridNum();
		}

		//有空余格子
		if(!isGridFull())
			return true;

		if(config.singlePlusMax==1 || config.enableTimeT!=null)
			return false;

		int itemPos=rData!=null ? rData.addIndex : -1;

		//没找到添加位置
		if(itemPos==-1)
		{
			return false;
		}
		else
		{
			ItemData tData=_list.get(itemPos);

			//是否绑定
			bool isBind=config.bindByGet || (data!=null && data.isBind);

			if(tData.isBind!=isBind)
			{
				return false;
			}

			if(config.singlePlusMax<=0)
				return true;

			return config.singlePlusMax-tData.num>=num;
		}
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

		ItemRecordData rData=getRecordDataAbs(id);

		//超出总上限
		if(config.totalPlusMax>0 && (rData.num+num)>config.totalPlusMax)
		{
			return false;
		}

		//是否单个叠加
		bool isSingleOne=config.singlePlusMax==1;
		//是否绑定
		bool isBind=config.bindByGet || (data!=null && data.isBind);
		//失效时间
		long disableTime=config.enableTimeT==null ? -1L : (data!=null ? data.disableTime : config.enableTimeT.getNextTime());

		int itemPos;
		int index=-1;
		int tempIndex;
		ItemData tData;
		int dNum;

		while(true)
		{
			//单个叠加
			if(isSingleOne)
			{
				if(isGridFull())
					return false;

				index=_nextFreeGridIndex;
				tData=null;
			}
			else
			{
				//没找到添加位置
				if((itemPos=rData.addIndex)==-1)
				{
					if(isGridFull())
						return false;

					index=_nextFreeGridIndex;
					tData=null;
				}
				else
				{
					//新值
					if(index<itemPos)
						index=itemPos;

					//找不到添加位置
					if((tempIndex=findNextItemAddPos(id,index,isBind,disableTime,data))==-1)
					{
						if(isGridFull())
							return false;

						index=_nextFreeGridIndex;
						tData=null;
					}
					else
					{
						tData=_list.get(index=tempIndex);
					}
				}
			}

			//要叠加
			if(tData!=null)
			{
				//装的下
				if(config.singlePlusMax<=0 || (dNum=(config.singlePlusMax-tData.num))>=num)
				{
					dNum=num;
					num=0;
				}
				else
				{
					num-=dNum;
				}

				tData.num+=dNum;//计数加

				//操作记录
				_operateRecordList.add2(index,dNum);
				doAddItemPartial(index,rData,dNum,tData);

				//完了
				if(num==0)
				{
					return true;
				}
			}
			//新位置
			else
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

				//最后的一次加
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

				//赋值
				tData.isBind=isBind;
				tData.disableTime=disableTime;

				_operateRecordList.add2(index,dNum);
				doAddItemNew(index,rData,dNum,tData);

				//完了
				if(num==0)
				{
					return true;
				}
			}
		}
	}

	protected override void doAddItemNew(int index,int num,ItemData data)
	{
		doAddItemNew(index,getRecordDataAbs(data.id),num,data);
	}

	/** 添加道具(新格子)(对应remove的completely) */
	private void doAddItemNew(int index,ItemRecordData rData,int num,ItemData data)
	{
		addItemToPos(index,data);

		//数目计数
		rData.num+=num;

		if(!data.config.enableTimeT.isEmpty())
		{
			_itemTimeSet.add(index);
		}

		_cleanUpDirty=true;

		//更新添加位置
		if(!data.isSingleNumMax())
		{
			if(rData.addIndex==-1 || index<rData.addIndex)
			{
				rData.addIndex=index;
			}
		}

		//更新移除位置
		if(rData.removeIndex==-1 || rData.removeIndex<index)
		{
			rData.removeIndex=index;
		}
	}

	protected override void doAddItemPartial(int index,int num,ItemData data)
	{
		doAddItemPartial(index,getRecordDataAbs(data.id),num,data);
	}

	/** 添加道具部分 */
	private void doAddItemPartial(int index,ItemRecordData rData,int num,ItemData data)
	{
		//数目计数
		rData.num+=num;

		//满了,还是当前位置
		if(data.isSingleNumMax() && rData.addIndex==index)
		{
			rData.addIndex=toCountNextItemAddPos(rData.id,index+1);
		}
	}

	/** 执行移除一个物品(倒序)(核心) */
	protected override bool doRemoveItemC(int id,int num)
	{
		ItemRecordData rData=getRecordData(id);

		if(rData==null || rData.num<num)
			return false;

		int index;
		ItemData data;
		int dNum;

		while(true)
		{
			index=rData.removeIndex;

			if(index!=-1)
			{
				data=_list.get(index);

				//还有剩余
				if(data.num>num)
				{
					dNum=num;
					data.num-=num;
					num=0;

					_operateRecordList.add2(index,dNum);
					doRemoveItemPartial(index,rData,dNum);
				}
				else
				{
					dNum=data.num;
					num-=data.num;

					_operateRecordList.add2(index,dNum);
					_operateRecordRemoveDic.put(index,data);
					data.canRelease=true;//回收标记

					doRemoveItemCompletely(index,rData,dNum,data);
				}

				if(num==0)
				{
					return true;
				}
			}
			else
			{
				Ctrl.throwError("严重错误,计数没对上");
				return false;
			}
		}
	}

	/** 移除执行序号物品数目(核心)(不回收) */
	protected override void doRemoveItemByIndexC(int index,int num,ItemData data)
	{
		//完全移除
		if(num==data.num)
		{
			_operateRecordList.add2(index,num);
			_operateRecordRemoveDic.put(index,data);
			doRemoveItemCompletely(index,getRecordDataAbs(data.id),num,data);
		}
		//部分移除
		else
		{
			data.num-=num;

			_operateRecordList.add2(index,num);
			doRemoveItemPartial(index,getRecordDataAbs(data.id),num);
		}
	}

	/** 部分移除一个格子 */
	protected override void doRemoveItemPartial(int index,int num,int id)
	{
		doRemoveItemPartial(index,getRecordDataAbs(id),num);
	}

	/** 部分移除一个格子 */
	private void doRemoveItemPartial(int index,ItemRecordData rData,int num)
	{
		//数目计数
		rData.num-=num;

		//更靠前
		if(rData.addIndex==-1 || index<rData.addIndex)
		{
			rData.addIndex=index;
		}
	}

	protected override void doRemoveItemCompletely(int index,int num,ItemData data)
	{
		doRemoveItemCompletely(index,getRecordDataAbs(data.id),num,data);
	}

	/** 完全移除一个格子(对应add的new) */
	private void doRemoveItemCompletely(int index,ItemRecordData rData,int num,ItemData data)
	{
		rData.num-=num;

		removeItemFromPos(index);

		//失效时间
		if(!data.config.enableTimeT.isEmpty())
		{
			_itemTimeSet.remove(index);
		}

		_cleanUpDirty=true;

		//没了
		if(rData.num==0)
		{
			removeRecordData(rData);
		}
		else
		{
			if(ShineSetting.openCheck)
			{
				if(rData.num<0)
				{
					Ctrl.throwError("出现道具数目为负");
				}
			}

			if(rData.addIndex==index)
			{
				rData.addIndex=toCountNextItemAddPos(rData.id,index+1);
			}

			if(rData.removeIndex==index)
			{
				rData.removeIndex=toCountPrevItemRemovePos(rData.id,index-1);
			}
		}
	}

	//接口组

	/** 获取某道具的总数目 */
	public override int getItemNum(int itemID)
	{
		ItemRecordData rData;

		if((rData=_itemRecordDic.get(itemID))!=null)
		{
			return rData.num;
		}

		return 0;
	}

	//add


	//use

	/** 通过ID使用物品 */
	public override bool useItemByID(int id,UseItemArgData arg)
	{
		ItemRecordData rData;

		if((rData=getRecordData(id))==null)
			return false;

		if(rData.num<=0 || rData.removeIndex==-1)
			return false;

		ItemData data;
		int index;

		if((data=_list.get(index=rData.removeIndex))==null)
		{
			Ctrl.throwError("不该找不到数据");
			return false;
		}

		return doUseItem(data,index,1,arg);
	}

	//clean

	/** 整理 */
	public void cleanUp()
	{
		//不需要整理
		if(!_cleanUpDirty)
			return;

		_cleanUpDirty=false;

		if(_list.isEmpty())
			return;

		if(CommonSetting.isClientDriveLogic)
		{
			cleanUpMethod1();
		}
		else
		{
			toSendCleanUp();
		}
	}

	/** 整理算法1(sort时处理堆叠,后剔除) */
	private void cleanUpMethod1()
	{
		//先排序
		_list.sort(compareItemForCleanUp1);

		ItemData[] values=_list.getValues();

		int len=_list.size();

		ItemData data;
		//上个物品数据
		ItemData lastData=null;

		//修正len
		for(int i=len-1;i>=0;--i)
		{
			if(values[i]!=null)
			{
				len=i+1;
				break;
			}
		}

		int allow;

		for(int i=0;i<len;i++)
		{
			if((data=values[i])!=null)
			{
				if(lastData==null)
				{
					//不满
					if(!data.isSingleNumMax())
					{
						lastData=data;
					}
				}
				else
				{
					//不满且可叠加
					if(canPlus(lastData,data))//!lastData.isSingleNumMax() &&
					{
						allow=lastData.config.singlePlusMax - lastData.num;

						if(data.num<=allow)
						{
							lastData.num+=data.num;
							data.num=0;
							values[i]=null;
							releaseItem(data);
							Array.Copy(values,i+1,values,i,len-i-1);
							--i;
							--len;

							if(lastData.isSingleNumMax())
							{
								lastData=null;
							}
						}
						else
						{
							lastData.num+=allow;
							data.num-=allow;
							lastData=data;
						}
					}
					else
					{
						lastData=data;
					}
				}
			}
		}

		//设置尺寸
		_list.justSetSize(len);

		reMakeData();
	}

	/** 相同ID道具的额外比较 */
	protected int compareItemEx(ItemData a1,ItemData a2)
	{
		return 0;
	}

	/** 比较物品,整理1用 */
	protected int compareItemForCleanUp1(ItemData a1,ItemData a2)
	{
		if(a2==null)
			return -1;

		if(a1==null)
			return 1;

		int compare;

		//先比序
		if((compare=MathUtils.intCompare(a1.config.sortIndex,a2.config.sortIndex))!=0)
			return compare;

		//再比id
		if((compare=MathUtils.intCompare(a1.config.id,a2.config.id))!=0)
			return compare;

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

		//多的在前
		return -MathUtils.intCompare(a1.num,a2.num);
	}

	/** 整理算法2(先处理堆叠,再排序,再剔除) */
	private void cleanUpMethod2()
	{

	}

	/** 移动/交换物品 */
	public void moveItem(int fromIndex,int toIndex)
	{
		ItemData item=getItem(fromIndex);

		if(item==null)
		{
			warnLog("移动物品时,找不到from物品",fromIndex);
			return;
		}

		if(!isIndexEnable(toIndex))
		{
			warnLog("移动物品时,toIndex不可用",toIndex);
			return;
		}

		if(fromIndex==toIndex)
		{
			warnLog("移动物品时,序号相等",fromIndex);
			return;
		}

		if(CommonSetting.isClientDriveLogic)
		{
			ItemData toItem=getItem(toIndex);

			if(toItem!=null)
			{
				int plusLast;
				//可叠加
				if(canPlus(toItem,item) && (plusLast=toItem.getPlusLast())!=0)
				{
					int dNum;
					//全部合并
					if(plusLast==-1 || item.num<=plusLast)
					{
						dNum=item.num;
					}
					else
					{
						dNum=plusLast;
					}

					removeItemByIndex(fromIndex,dNum,CallWayType.MoveItem);

					//操作记录
					_operateRecordList.add2(toIndex,dNum);
					toItem.num+=dNum;
					doAddItemPartial(toIndex,dNum,toItem);
					flushAdd(CallWayType.MoveItem);

					return;
				}
			}

			//走交换逻辑
			toSwapItem(fromIndex,toIndex,item,toItem);
		}
		else
		{
			me.send(FuncMoveItemRequest.create(_funcID,fromIndex,toIndex));
		}
	}

	private void toSwapItem(int fromIndex,int toIndex,ItemData item,ItemData toItem)
	{
		bool toHasTime=false;
		//交换
		if(toItem!=null)
		{
			_list.set(toIndex,null);
			toHasTime=_itemTimeSet.remove(toIndex);
		}

		bool fromHasTime=_itemTimeSet.remove(fromIndex);

		_list.set(fromIndex,toItem);

		if(toItem!=null)
		{
			toItem.index=fromIndex;

			if(toHasTime)
			{
				_itemTimeSet.add(fromIndex);
			}
		}

		//确认尺寸
		_list.growSize(toIndex+1);

		_list.set(toIndex,item);
		item.index=toIndex;

		if(fromHasTime)
		{
			_itemTimeSet.add(toIndex);
		}

		if(toItem!=null)
		{
			reCountRecordIndex(getRecordData(toItem.id),toIndex,fromIndex);
		}
		else
		{
			//先加再删
			toCountFreeGridByAdd(toIndex);
			toCountFreeGridByRemove(fromIndex);
		}

		reCountRecordIndex(getRecordData(item.id),fromIndex,toIndex);
	}

	private void reCountRecordIndex(ItemRecordData rData,int fromIndex,int toIndex)
	{
		//左移
		if(toIndex<fromIndex)
		{
			if(rData.addIndex==-1 || toIndex<rData.addIndex)
			{
				//直接重新计算
				rData.addIndex=toCountNextItemAddPos(rData.id,toIndex);
			}

			//是之前的删除位置
			if(fromIndex==rData.removeIndex)
			{
				//直接重新计算
				rData.removeIndex=toCountPrevItemRemovePos(rData.id,fromIndex-1);
			}
		}
		//右移
		else
		{
			if(rData.removeIndex==-1 || toIndex>rData.removeIndex)
			{
				//直接重新计算
				rData.removeIndex=toCountPrevItemRemovePos(rData.id,toIndex);
			}

			//是之前的添加位置
			if(fromIndex==rData.addIndex)
			{
				//直接重新计算
				rData.addIndex=toCountNextItemAddPos(rData.id,fromIndex+1);
			}
		}
	}

	/** 服务器交换物品 */
	public void onServerSwapItem(int fromIndex,int toIndex)
	{
		ItemData item;
		if((item=getItem(fromIndex))==null)
		{
			Ctrl.warnLog("收到swapItem时fromIndex为空",fromIndex);
			return;
		}

		ItemData toItem;
		if((toItem=getItem(toIndex))==null)
		{
			Ctrl.warnLog("收到swapItem时toIndex为空",toIndex);
			return;
		}

		toSwapItem(fromIndex,toIndex,item,toItem);
	}

	/** 拆分物品到空闲格子 */
	public void splitItem(int index,int num)
	{
		ItemData item=getItem(index);

		if(item==null)
		{
			warnLog("拆分物品时,找不到from物品",index);
			return;
		}

		if(num<=0 || num>(item.num-1))
		{
			warnLog("拆分物品时,数目非法",num);
			return;
		}

		if(CommonSetting.isClientDriveLogic)
		{
			ItemData nItem=(ItemData)item.clone();
			nItem.num=num;

			removeItemByIndex(index,num,CallWayType.SplitItem);
			addNewItemToIndex(_nextFreeGridIndex,nItem,CallWayType.SplitItem);
		}
		else
		{
			me.send(FuncSplitItemRequest.create(_funcID,index,num));
		}
	}

	/** 打印背包 */
	public override void printBag()
	{
		if(isEmpty())
		{
			Ctrl.print("bag is empty");
			return;
		}

		ObjectUtils.printDataList(_list);
	}

	/** 警告日志 */
	public virtual void warnLog(params object[] args)
	{
		Ctrl.warnLog(args);
	}

	//client

	protected override void updateItem(int index,ItemData data)
	{
		_list.set(index,data);
	}

	public void onRefreshGridNumByServer(int gridNum)
	{
		setGridNum(gridNum);
	}

	/** 服务器整理背包 */
	public void onCleanUpByServer(SList<ItemData> list)
	{
		_data.items=_list=list;

		_cleanUpDirty=true;

		reMakeData();

		onRefreshAll();
	}

	//接口组



	/** 发送清理背包 */
	protected virtual void toSendCleanUp()
	{

	}

	/** 获取显示列表 */
	public override SList<ItemData> getShowList()
	{
		return _list;
	}
}