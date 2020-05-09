using System;
using ShineEngine;

/// <summary>
/// 翻页显示插件
/// </summary>
public class PlayerSubsectionPageShowTool:PlayerFuncTool
{
	/** 小组index */
	private int _subsectionSubIndex;
	/** 大组index */
	private int _subsectionIndex;
	/** 每页显示数目 */
	private int _eachPageShowMax;
	/** 翻页缓存数据 */
	private LongObjectMap<CachePageData> _cachePageDataMap=new LongObjectMap<CachePageData>();

	public class CachePageData
	{
		/** 大组index */
		public int subsectionIndex;
		/** 小组index */
		public int subsectionSubIndex;
		/** 数据 */
		public SList<KeyData> list=new SList<KeyData>();
		/** 锁字典 */
		public IntIntMap lockDic=new IntIntMap();
		/** 需要提送标记 */
		public IntSet needSendSet=new IntSet();

		public int currentPage=-1;

		/** 当前参数 */
		public int arg;
	}

	public PlayerSubsectionPageShowTool(int funcID,int eachPageShowMax):base(FuncToolType.SubsectionPageShow,funcID)
	{
		_eachPageShowMax=eachPageShowMax;
	}

	public void setSubsectionIndex(int subsectionIndex,int subsectionSubIndex)
	{
		_subsectionIndex = subsectionIndex;
		_subsectionSubIndex = subsectionSubIndex;
	}

	private long getCacheKey(int subsectionIndex,int subsectionSubIndex)
	{
		return (long)subsectionIndex << 16 | (long)subsectionSubIndex;
	}

	private CachePageData getCachePageData(int subsectionIndex,int subsectionSubIndex)
	{
		long cacheKey = getCacheKey(subsectionIndex, subsectionSubIndex);

		CachePageData cachePageData;

		if (!_cachePageDataMap.contains(cacheKey))
		{
			_cachePageDataMap.put(cacheKey,cachePageData=new CachePageData());
			cachePageData.subsectionIndex = subsectionIndex;
			cachePageData.subsectionSubIndex = subsectionSubIndex;
		}
		else
		{
			cachePageData = _cachePageDataMap.get(cacheKey);
		}

		return cachePageData;
	}
	
	/** 接受数据 */
	public void onReceivePage(int subsectionIndex,int subsectionSubIndex,int page,IntObjectMap<KeyData> dic,IntSet changePageSet)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		if(changePageSet!=null && !changePageSet.isEmpty())
		{
			changePageSet.forEach(k=>
			{
				//清CD
				cachePageData.lockDic.put(k,0);
			});
		}

		SList<KeyData> list=cachePageData.list;

		if(dic!=null)
		{
			dic.forEach((k,v)=>
			{
				ensureSize(k,list);
				list.set(k,v);
			});

			//有值才刷新
			onRefreshPage(subsectionIndex,subsectionSubIndex,page);
		}
	}

	/** 接受数据 */
	public void onReceivePageList(int subsectionIndex,int subsectionSubIndex,int page,SList<KeyData> receiveList)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		//清cd
		cachePageData.lockDic.put(page,0);

		SList<KeyData> list=cachePageData.list;

		if(receiveList!=null && !receiveList.isEmpty())
		{
			int from=page * _eachPageShowMax;
			ensureSize(from+receiveList.size()-1,list);

			KeyData[] values=receiveList.getValues();
			KeyData v;

			for(int i=0,len=receiveList.size();i<len;++i)
			{
				v=values[i];

				list.set(from+i,v);
			}
		}
		
		Ctrl.print("onReceivePageList,",list.length());
	}

	/** 确保index能放下 */
	private void ensureSize(int index,SList<KeyData> slist)
	{
		SList<KeyData> list;

		if((list=slist).size()>index)
			return;

		for(int i=index-list.size();i>=0;--i)
		{
			list.add(null);
		}
	}

	/** 设置当前参数 */
	public void setArg(int subsectionIndex,int subsectionSubIndex,int arg)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		if(cachePageData.arg==arg)
			return;

		cachePageData.arg=arg;

		refresh(subsectionIndex,subsectionSubIndex);
	}

	/** 关闭当前页 */
	public void closePage(int subsectionIndex,int subsectionSubIndex)
	{
		setCurrentPage(subsectionIndex,subsectionSubIndex,-1);
	}
	
	/** 关闭当前页 */
	public void closePage()
	{
		setCurrentPage(_subsectionIndex,_subsectionSubIndex,-1);
	}

	public void setCurrentPage(int page)
	{
		setCurrentPage(_subsectionIndex,_subsectionSubIndex,page);
	}

	/** 选择当前观察页(用完了传-1) */
	public void setCurrentPage(int subsectionIndex,int subsectionSubIndex,int page)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		if(cachePageData.currentPage==page)
			return;

		cachePageData.currentPage=page;

		if(page!=-1)
		{
			getOnePage(subsectionIndex,subsectionSubIndex,page);
		}
	}

	/** 获取某页数据(调用) */
	private void getOnePage(int subsectionIndex,int subsectionSubIndex,int page)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		/** 未到时间 */
		if(cachePageData.lockDic.get(page)>0)
		{
			cachePageData.needSendSet.add(page);
		}
		else
		{
			cachePageData.lockDic.put(page,Global.pageToolShowCD);
			sendGet(subsectionIndex,subsectionSubIndex,page,cachePageData.arg);
		}
	}

	/** 每秒调用 */
	public override void onSecond(int delay)
	{
		_cachePageDataMap.forEachValue(v =>
		{
			IntSet needSendSet=v.needSendSet;
			IntIntMap dic=v.lockDic;

			int fv=dic.getFreeValue();
			int[] keys=dic.getKeys();
			int[] values=dic.getValues();
			int key;

			for(int i=keys.Length-1;i>=0;--i)
			{
				if((key=keys[i])!=fv)
				{
					if(values[i]>0)
					{
						if(--values[i]==0)
						{
							if(needSendSet.contains(key))
							{
								needSendSet.remove(key);

								//是当前页才发送
								if(v.currentPage==key)
								{
									values[i]=Global.pageToolShowCD;
									sendGet(v.subsectionIndex,v.subsectionSubIndex,key,v.arg);
								}
							}
						}
					}
				}
			}
		});
	}

	/** 推送获取 */
	private void sendGet(int subsectionIndex,int subsectionSubIndex,int page,int arg)
	{
		FuncGetSubsectionPageShowRequest.create(_funcID,subsectionIndex,subsectionSubIndex,page,arg).send();
	}

	/** 刷新页码 */
	private void onRefreshPage(int subsectionIndex,int subsectionSubIndex,int page)
	{
		me.dispatch(GameEventType.FuncRefreshSubsectionPageShow,new int[]{_funcID,subsectionIndex,subsectionSubIndex,page});
	}

	/** 获取当前数据 */
	public SList<KeyData> getList(int subsectionIndex,int subsectionSubIndex)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		return cachePageData.list;
	}

	public SList<KeyData> getList()
	{
		return getList(_subsectionIndex,_subsectionSubIndex);
	}

	/** 获取每页显示上限 */
	public int getEachPageShowMax()
	{
		return _eachPageShowMax;
	}

	/** 清空数据并重新获取 */
	public void clear()
	{
		_cachePageDataMap.forEach((k,v) =>
		{
			v.list.clear();
			v.lockDic.clear();
			v.needSendSet.clear();
		});
	}

	/** 重新获取 */
	public void refresh(int subsectionIndex,int subsectionSubIndex)
	{
		CachePageData cachePageData=getCachePageData(subsectionIndex, subsectionSubIndex);
		
		cachePageData.lockDic.clear();
		cachePageData.needSendSet.clear();

		if(cachePageData.currentPage!=-1)
		{
			getOnePage(subsectionIndex,subsectionSubIndex,cachePageData.currentPage);
		}
	}
}