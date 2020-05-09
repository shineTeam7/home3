package com.home.commonBase.tool;

import com.home.commonBase.data.system.WorkData;
import com.home.commonBase.data.system.WorkReceiverData;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.control.DateControl;
import com.home.shine.support.collection.LongLongMap;
import com.home.shine.utils.TimeUtils;

/** 事务接收工具 */
public class WorkReceiveTool
{
	private WorkReceiverData _data;
	
	public void setData(WorkReceiverData data)
	{
		_data=data;
	}
	
	/** 每日删除 */
	public void onDaily()
	{
		if(_data==null)
			return;
		
		long removeTime=DateControl.getTimeMillis()-(CommonSetting.workReceiveTimeOut*TimeUtils.dayTime);
		
		_data.workExecuteRecordDic.forEachValue(dic->
		{
			dic.forEachS((k,v)->
			{
				//超时
				if(v<removeTime)
				{
					dic.remove(k);
				}
			});
		});
	}
	
	/** 记录一个,返回是否记录成功(false为已存在) */
	public boolean record(WorkData data)
	{
		long instanceID=data.workInstanceID;
		
		LongLongMap dic=_data.workExecuteRecordDic.get(data.senderIndex);
		
		if(dic==null)
		{
			_data.workExecuteRecordDic.put(data.senderIndex,dic=new LongLongMap());
		}
		
		return dic.putIfAbsent3(instanceID,data.timestamp);
	}
	
	/** 完成接收 */
	public void onCompleteReceipt(long workInstanceID,int senderIndex)
	{
		LongLongMap dic=_data.workExecuteRecordDic.get(senderIndex);
		
		if(dic==null)
			return;
		
		//移除记录
		dic.remove(workInstanceID);
	}
}
