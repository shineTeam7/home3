package com.home.commonBase.tool;

import com.home.commonBase.dataEx.role.StatusCalculateInfo;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntBooleanMap;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntList;

/** 字典实现状态工具 */
public abstract class StatusToolForDic
{
	private StatusCalculateInfo _info;
	
	private boolean _isM;
	
	/** 状态组 */
	private IntBooleanMap _statusDataDic;
	
	/** status统计组 */
	private IntIntMap _statusCounts;
	/** 改变组 */
	private IntList _changeList;
	
	/** 状态推送标记 */
	protected boolean _dirty=false;
	/** 上次的状态组(推送用) */
	private IntBooleanMap _lastStatus;
	/** 改变组 */
	private boolean[] _changeSet;
	
	public void setInfo(com.home.commonBase.dataEx.role.StatusCalculateInfo info)
	{
		_info=info;
		
		_statusCounts=new IntIntMap();
		_changeList=new IntList();
		_lastStatus=new IntBooleanMap();
		
		_changeSet=new boolean[info.size];
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
			IntBooleanMap lastStatus=_lastStatus;
			lastStatus.clear();
			
			IntIntMap statusCounts=_statusCounts;
			statusCounts.clear();
			
			dic.forEach((k,v)->
			{
				if(v)
				{
					statusCounts.put(k,1);
					lastStatus.put(k,true);
				}
			});
			
			_dirty=false;
		}
	}
	
	//方法组
	
	/** 获取状态计数组 */
	public IntIntMap getStatusCounts()
	{
		return _statusCounts;
	}
	
	/** 状态清理到默认值 */
	public void clear()
	{
		//初值
		_statusDataDic.clear();
		
		_lastStatus.clear();
		_statusCounts.clear();
		
		boolean[] changeSet=_changeSet;
		
		for(int i=_info.size - 1;i >= 0;--i)
		{
			changeSet[i]=false;
		}
		
		_dirty=false;
	}
	
	private void writeSendDic(int[] list)
	{
		IntBooleanMap dic=_statusDataDic;
		dic.clear();
		
		IntIntMap statusCounts=_statusCounts;
		int type;
		
		for(int i=list.length - 1;i >= 0;--i)
		{
			if((statusCounts.get(type=list[i]))>0)
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
		
		IntIntMap statusCounts=_statusCounts;
		
		statusCounts.forEach((k,v)->
		{
			if(v>0)
				dic.put(k,true);
		});
	}
	
	/** 获取状态 */
	public boolean getStatus(int type)
	{
		return _statusCounts.get(type)>0;
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
		
		IntBooleanMap lastStatus=_lastStatus;
		IntIntMap statusCounts=_statusCounts;
		IntList changeList=_changeList;
		changeList.clear();
		
		boolean[] changeSet=_changeSet;
		
		
		int[] allList=_info.allList;
		
		boolean[] sendSelfSet=_info.sendSelfSet;
		boolean[] sendOtherSet=_info.sendOtherSet;
		
		IntBooleanMap sendSelfDic=null;
		IntBooleanMap sendOtherDic=null;
		
		for(int i=allList.length - 1;i >= 0;--i)
		{
			type=allList[i];
			
			if(lastStatus.get(type)!=(value=statusCounts.get(type)>0))
			{
				lastStatus.put(type,value);
				
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
				
				changeList.add(type);
				changeSet[type]=true;
			}
		}
		
		if(sendSelfDic!=null)
			toSendSelf(sendSelfDic);
		
		if(sendOtherDic!=null)
			toSendOther(sendOtherDic);
		
		if(!changeList.isEmpty())
		{
			try
			{
				toDispatch(changeSet);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			int[] values=changeList.getValues();
			
			for(int i=changeList.length()-1;i>=0;--i)
			{
				changeSet[values[i]]=false;
			}
			
			changeList.clear();
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
		_statusCounts.put(type,value ? 1 : 0);
		_dirty=true;
	}
	
	/** 加状态 */
	public void addStatus(int type)
	{
		if(_statusCounts.addValue(type,1)==1)
		{
			_dirty=true;
		}
	}
	
	/** 减状态 */
	public void subStatus(int type)
	{
		if(_statusCounts.addValueR(type,-1)==0)
		{
			_dirty=true;
		}
		else
		{
			_statusCounts.remove(type);
			Ctrl.throwError("状态已无法再减",type);
			return;
		}
		
		if(_statusCounts.get(type)==0)
		{
			Ctrl.throwError("状态已无法再减",type);
			return;
		}
	}
	
	/** 服务器设置属性 */
	public void setStatusByServer(IntBooleanMap dic)
	{
		if(!dic.isEmpty())
		{
			IntIntMap statusCounts=_statusCounts;
			statusCounts.clear();
			
			dic.forEach((k,v)->
			{
				if(v)
				{
					statusCounts.put(k,1);
				}
			});
			
			doRefreshStatus();
		}
	}
}
