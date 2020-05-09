using System;
using ShineEngine;

/// <summary>
/// 翻页显示插件
/// </summary>
public class PlayerPageShowTool:PlayerFuncTool
{
	/** 每页显示数目 */
	private int _eachPageShowMax;
	
	/** 数据 */
	private SList<KeyData> _list=new SList<KeyData>();
	/** 锁字典 */
	private IntIntMap _lockDic=new IntIntMap();
	/** 需要提送标记 */
	private IntSet _needSendSet=new IntSet();

	private int _currentPage=-1;

	/** 当前参数 */
	private int _arg;

	public PlayerPageShowTool(int funcID,int eachPageShowMax):base(FuncToolType.PageShow,funcID)
	{
		_eachPageShowMax=eachPageShowMax;
	}

	/** 接受数据 */
	public void onReceivePage(int page,IntObjectMap<KeyData> dic,IntSet changePageSet)
	{
		if(changePageSet!=null && !changePageSet.isEmpty())
		{
			changePageSet.forEach(k=>
			{
				//清CD
				_lockDic.put(k,0);
			});
		}

		SList<KeyData> list=_list;

		if(dic!=null)
		{
			dic.forEach((k,v)=>
			{
				ensureSize(k);
				list.set(k,v);
			});

			//有值才刷新
			onRefreshPage(page);
		}
	}

	/** 接受数据 */
	public void onReceivePageList(int page,SList<KeyData> receiveList)
	{
		//清cd
		_lockDic.put(page,0);

		SList<KeyData> list=_list;

		if(receiveList!=null && !receiveList.isEmpty())
		{
			int from=page * _eachPageShowMax;
			ensureSize(from+receiveList.size()-1);

			KeyData[] values=receiveList.getValues();
			KeyData v;

			for(int i=0,len=receiveList.size();i<len;++i)
			{
				v=values[i];

				list.set(from+i,v);
			}
		}
	}

	/** 确保index能放下 */
	private void ensureSize(int index)
	{
		SList<KeyData> list;

		if((list=_list).size()>index)
			return;

		for(int i=index-list.size();i>=0;--i)
		{
			list.add(null);
		}
	}

	/** 设置当前参数 */
	public void setArg(int arg)
	{
		if(_arg==arg)
			return;

		_arg=arg;

		refresh();
	}

	/** 关闭当前页 */
	public void closePage()
	{
		setCurrentPage(-1);
	}

	/** 选择当前观察页(用完了传-1) */
	public void setCurrentPage(int page)
	{
		if(_currentPage==page)
			return;

		_currentPage=page;

		if(page!=-1)
		{
			getOnePage(page);
		}
	}

	/** 获取某页数据(调用) */
	private void getOnePage(int page)
	{
		/** 未到时间 */
		if(_lockDic.get(page)>0)
		{
			_needSendSet.add(page);
		}
		else
		{
			_lockDic.put(page,Global.pageToolShowCD);
			sendGet(page);
		}
	}

	/** 每秒调用 */
	public override void onSecond(int delay)
	{
		IntSet needSendSet=_needSendSet;
		IntIntMap dic=_lockDic;

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
							if(_currentPage==key)
							{
								values[i]=Global.pageToolShowCD;
								sendGet(key);
							}
						}
					}
				}
			}
		}
	}

	/** 推送获取 */
	private void sendGet(int page)
	{
		FuncGetPageShowRequest.create(_funcID,page,_arg).send();
	}

	/** 刷新页码 */
	private void onRefreshPage(int page)
	{
		me.dispatch(GameEventType.FuncRefreshPageShow,new int[]{_funcID,page});
	}

	/** 获取当前数据 */
	public SList<KeyData> getList()
	{
		return _list;
	}

	/** 获取每页显示上限 */
	public int getEachPageShowMax()
	{
		return _eachPageShowMax;
	}

	/** 清空数据并重新获取 */
	public void clear()
	{
		_list.clear();
		refresh();
	}

	/** 重新获取 */
	public void refresh()
	{
		_lockDic.clear();
		_needSendSet.clear();

		if(_currentPage!=-1)
		{
			getOnePage(_currentPage);
		}
	}
}