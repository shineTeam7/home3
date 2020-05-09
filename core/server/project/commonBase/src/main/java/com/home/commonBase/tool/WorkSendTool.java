package com.home.commonBase.tool;

import com.home.commonBase.data.system.WorkData;
import com.home.commonBase.data.system.WorkSenderData;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.LongObjectMap;

/** 事务发起工具 */
public abstract class WorkSendTool
{
	private String _name;
	
	private WorkSenderData _data;
	
	/** 事务链表头 */
	private WorkData _workHead;
	/** 事务链表尾 */
	private WorkData _workTail;
	
	public WorkSendTool(String name)
	{
		_name=name;
	}
	
	/** 设置数据 */
	public void setData(WorkSenderData data)
	{
		_data=data;
		
		LongObjectMap<WorkData> workRecordDic=data.workRecordDic;
		long[] values=workRecordDic.getSortedKeyList().getValues();
		WorkData workData;
		
		for(int i=0,len=workRecordDic.size();i<len;++i)
		{
			workData=workRecordDic.get(values[i]);
			workData.lastCheckTime=DateControl.getTimeMillis();
			addToList(workData);
		}
	}
	
	public void onSecond(int delay)
	{
		if(_workHead!=null)
		{
			long now=DateControl.getTimeMillis();
			
			WorkData data=_workHead;
			WorkData temp;
			
			while(data!=null)
			{
				//先保存下一个,因为当前节点有可能被删
				temp=data.next;
				
				if((now-data.lastCheckTime)>= CommonSetting.workCheckReSendDelay)
				{
					data.lastCheckTime=now;
					
					Ctrl.warnLog(_name+"重放一个事务,instanceID:",data.workInstanceID,"dataID:",data.getDataID(),"senderIndex:",data.senderIndex);
					//重放
					resendWork(data);
				}
				
				data=temp;
			}
		}
	}
	
	/** 分配序号，并记录事务 */
	public void recordWork(WorkData work)
	{
		if(ShineSetting.openCheck)
		{
			if(work.prev!=null || work.next!=null)
			{
				Ctrl.throwError("重复添加事务记录",work);
			}
		}
		
		work.workInstanceID=++_data.workInstanceID;
		_data.workRecordDic.put(work.workInstanceID,work);
		//当前时间
		work.lastCheckTime=DateControl.getTimeMillis();
		
		addToList(work);
	}
	
	private void addToList(WorkData work)
	{
		if(_workHead==null)
		{
			_workTail=work;
			_workHead=work;
		}
		else
		{
			_workTail.next=work;
			work.prev=_workTail;
			_workTail=work;
		}
	}
	
	private void removeFromList(WorkData work)
	{
		if(work.prev!=null)
		{
			if(ShineSetting.openCheck)
			{
				if(work.prev.next!=work)
				{
					Ctrl.errorLog(_name+"链表节点不对",work.workInstanceID);
				}
			}
			
			work.prev.next=work.next;
		}
		
		if(work.next!=null)
		{
			if(ShineSetting.openCheck)
			{
				if(work.next.prev!=work)
				{
					Ctrl.errorLog(_name+"链表节点不对2",work.workInstanceID);
				}
			}
			
			work.next.prev=work.prev;
		}
		
		if(work==_workTail)
		{
			_workTail=work.prev;
		}
		
		if(work==_workHead)
		{
			_workHead=work.next;
		}
		
		work.prev=null;
		work.next=null;
	}
	
	/** 接受回执 */
	public WorkData onReceiptWork(long instanceID)
	{
		//主线程才可以
		ThreadControl.checkCurrentIsMainThread();
		
		WorkData work;
		
		if((work=_data.workRecordDic.remove(instanceID))==null)
		{
			Ctrl.warnLog(_name+"接受事务回执时,事务不存在",instanceID);
			return null;
		}
		
		removeFromList(work);
		
		completeWork(work);
		
		return work;
	}
	
	/** 重放事务 */
	protected abstract void resendWork(WorkData data);
	
	/** 事务完成 */
	protected abstract void completeWork(WorkData data);
}
