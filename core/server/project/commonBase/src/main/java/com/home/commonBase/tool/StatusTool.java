package com.home.commonBase.tool;

import com.home.commonBase.dataEx.role.StatusCalculateInfo;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntBooleanMap;

/** 状态工具 */
public abstract class StatusTool
{
	private StatusCalculateInfo _info;
	
	private boolean _isM;
	
	/** 状态组 */
	private IntBooleanMap _statusDataDic;
	/** buff统计组 */
	private int[] _statusCounts;
	/** 改变组 */
	private int[] _changeList;
	
	/** 状态推送标记 */
	protected boolean _dirty=false;
	/** 上次的状态组(推送用) */
	private boolean[] _lastStatus;
	/** 改变组 */
	private boolean[] _changeSet;
	
	public void setInfo(StatusCalculateInfo info)
	{
		_info=info;
		
		_statusCounts=new int[info.size];
		_changeList=new int[info.size];
		_changeSet=new boolean[info.size];
		_lastStatus=new boolean[info.size];
	}
	
	public void setIsM(boolean value)
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
			boolean[] lastStatus=_lastStatus;
			int[] statusCounts=_statusCounts;
			
			dic.forEach((k,v)->
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
		
		boolean[] lastStatus=_lastStatus;
		int[] statusCounts=_statusCounts;
		boolean[] changeSet=_changeSet;
		
		for(int i=_info.size - 1;i >= 0;--i)
		{
			lastStatus[i]=false;
			statusCounts[i]=0;
			changeSet[i]=false;
		}
		
		_dirty=false;
	}
	
	private void writeSendDic(int[] list)
	{
		IntBooleanMap dic=_statusDataDic;
		dic.clear();
		
		int[] statusCounts=_statusCounts;
		int type;
		int v;
		
		for(int i=list.length - 1;i >= 0;--i)
		{
			if((statusCounts[type=list[i]])>0)
				dic.put(type,true);
		}
	}
	
	public void writeForSelf()
	{
		writeSendDic(_info.sendSelfList);
	}
	
	public void writeForOther()
	{
		writeSendDic(_info.sendOtherList);
	}
	
	/** 写入拷贝 */
	public void writeForCopy()
	{
		IntBooleanMap dic=_statusDataDic;
		dic.clear();
		
		int[] statusCounts=_statusCounts;
		
		for(int i=statusCounts.length - 1;i >= 0;--i)
		{
			if(statusCounts[i]>0)
				dic.put(i,true);
		}
	}
	
	/** 获取状态 */
	public boolean getStatus(int type)
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
		boolean value;
		int num=0;
		
		boolean[] lastStatus=_lastStatus;
		int[] statusCounts=_statusCounts;
		int[] changeList=_changeList;
		boolean[] changeSet=_changeSet;
		
		int[] allList=_info.allList;
		
		boolean[] sendSelfSet=_info.sendSelfSet;
		boolean[] sendOtherSet=_info.sendOtherSet;
		
		IntBooleanMap sendSelfDic=null;
		IntBooleanMap sendOtherDic=null;
		
		for(int i=allList.length - 1;i >= 0;--i)
		{
			type=allList[i];
			
			if(lastStatus[type]!=(value=statusCounts[type]>0))
			{
				lastStatus[type]=value;
				
				if(_isM)
				{
					if(sendSelfSet[type])
					{
						if(sendSelfDic==null)
							sendSelfDic=new IntBooleanMap();
						
						sendSelfDic.put(type,value);
					}
				}
				
				if(sendOtherSet[type])
				{
					if(sendOtherDic==null)
						sendOtherDic=new IntBooleanMap();
					
					sendOtherDic.put(type,value);
				}
				
				changeList[num++]=type;
				changeSet[type]=true;
			}
		}
		
		if(sendSelfDic!=null)
			toSendSelf(sendSelfDic);
		
		if(sendOtherDic!=null)
			toSendOther(sendOtherDic);
		
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
	
	/** 推送自己 */
	abstract protected void toSendSelf(IntBooleanMap dic);
	/** 广播别人 */
	abstract protected void toSendOther(IntBooleanMap dic);
	/** 派发刷新属性 */
	abstract protected void toDispatch(boolean[] changeSet);
	
	/** 设置状态(只系统用) */
	public void setStatus(int type,boolean value)
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
			boolean[] lastStatus=_lastStatus;
			int[] statusCounts=_statusCounts;
			int[] changeList=_changeList;
			boolean[] changeSet=_changeSet;
			
			dic.forEach((k,v)->
			{
				statusCounts[k]=v ? 1 : 0;
			});
			
			doRefreshStatus();
		}
	}
}
