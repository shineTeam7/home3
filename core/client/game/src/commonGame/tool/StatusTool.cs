using System;
using ShineEngine;

/// <summary>
/// 状态工具
/// </summary>
public abstract class StatusTool
{
	private StatusCalculateInfo _info;

	private bool _isM;

	/** 状态组 */
	private IntBooleanMap _statusDataDic;
	/** buff统计组 */
	private int[] _statusCounts;
	/** 改变组 */
	private int[] _changeList;

	/** 状态推送标记 */
	protected bool _dirty=false;
	/** 上次的状态组(推送用) */
	private bool[] _lastStatus;
	/** 改变组 */
	private bool[] _changeSet;

	public void setInfo(StatusCalculateInfo info)
	{
		_info=info;

		_statusCounts=new int[info.size];
		_changeList=new int[info.size];
		_changeSet=new bool[info.size];
		_lastStatus=new bool[info.size];
	}

	public void setIsM(bool value)
	{
		_isM=value;
	}

	/** 设置数据 */
	public void setData(IntBooleanMap dic)
	{
		_statusDataDic=dic;

		if(dic!=null && !dic.isEmpty())
		{
			//statusCount由buff生成
			bool[] lastStatus=_lastStatus;
			int[] statusCounts=_statusCounts;

			dic.forEach((k,v)=>
			{
				statusCounts[k]=v ? 1 : 0;
				lastStatus[k]=v;
			});

			_dirty=false;
		}
	}

	//方法组

	/** 获取状态计数组 */
	public int[] getStatusCounts()
	{
		return _statusCounts;
	}

	/** 状态清理到默认值 */
	public void clear()
	{
		//初值
		_statusDataDic.clear();

		bool[] lastStatus=_lastStatus;
		int[] statusCounts=_statusCounts;
		bool[] changeSet=_changeSet;

		for(int i=_info.size - 1;i >= 0;--i)
		{
			lastStatus[i]=false;
			statusCounts[i]=0;
			changeSet[i]=false;
		}

		_dirty=false;
	}

	/** 写入拷贝 */
	public void writeForCopy()
	{
		IntBooleanMap dic=_statusDataDic;
		dic.clear();

		int[] statusCounts=_statusCounts;

		for(int i=statusCounts.Length - 1;i >= 0;--i)
		{
			if(statusCounts[i]>0)
				dic.put(i,true);
		}
	}

	/** 获取状态 */
	public bool getStatus(int type)
	{
		return _statusCounts[type]>0;
	}


	/** 刷新状态组 */
	public void refreshStatus()
	{
		if(_dirty)
		{
			doRefreshStatus();
		}
	}

	private void doRefreshStatus()
	{
		_dirty=false;

		int type;
		bool value;
		int num=0;

		bool[] lastStatus=_lastStatus;
		int[] statusCounts=_statusCounts;
		int[] changeList=_changeList;
		bool[] changeSet=_changeSet;

		int[] allList=_info.allList;

		IntBooleanMap sendSelfDic=null;
		IntBooleanMap sendOtherDic=null;

		for(int i=allList.Length - 1;i >= 0;--i)
		{
			type=allList[i];

			if(lastStatus[type]!=(value=statusCounts[type]>0))
			{
				lastStatus[type]=value;

				changeList[num++]=type;
				changeSet[type]=true;
			}
		}

		if(num>0)
		{
			try
			{
				toDispatch(changeSet);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}

			for(int i=num-1;i>=0;--i)
			{
				changeSet[changeList[i]]=false;
			}
		}
	}

	/** 派发刷新属性 */
	abstract protected void toDispatch(bool[] changeSet);

	/** 设置状态(只系统用) */
	public void setStatus(int type,bool value)
	{
		_statusCounts[type]=value ? 1 : 0;
		_dirty=true;
	}

	/** 加状态 */
	public void addStatus(int type)
	{
		if(++_statusCounts[type]==1)
		{
			_dirty=true;
		}
	}

	/** 减状态 */
	public void subStatus(int type)
	{
		if(_statusCounts[type]==0)
		{
			Ctrl.throwError("状态已无法再减",type);
			return;
		}

		if(--_statusCounts[type]==0)
		{
			_dirty=true;
		}
	}

	/** 服务器设置属性 */
	public void setStatusByServer(IntBooleanMap dic)
	{
		if(!dic.isEmpty())
		{
			int num=0;
			bool[] lastStatus=_lastStatus;
			int[] statusCounts=_statusCounts;
			int[] changeList=_changeList;
			bool[] changeSet=_changeSet;

			//TODO:继续

			foreach(var kv in dic.entrySet())
			{
				statusCounts[kv.key]=kv.value ? 1 : 0;
			}

			doRefreshStatus();
		}
	}
}