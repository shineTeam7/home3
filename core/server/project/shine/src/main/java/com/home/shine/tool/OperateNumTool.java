package com.home.shine.tool;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.LongQueue;
import com.home.shine.timer.ITimeEntity;
import com.home.shine.utils.TimeUtils;

/** 操作次数超时工具 */
public class OperateNumTool
{
	private ITimeEntity _timeEntity;
	
	private LongQueue _queue;
	private boolean _selfInited=false;
	
	private long _timeOut;
	
	public OperateNumTool(ITimeEntity entity)
	{
		this(entity,TimeUtils.dayTime*2);//48小时
	}
	
	public OperateNumTool(ITimeEntity entity,long timeOut)
	{
		_timeEntity=entity;
		_timeOut=timeOut;
	}
	
	public void setData(LongQueue queue)
	{
		_queue=queue;
	}
	
	public void init()
	{
		_selfInited=true;
		_queue=new LongQueue();
	}
	
	public void check()
	{
		if(_queue.isEmpty())
			return;
		
		long now=_timeEntity.getTimeMillis();
		
		while(!_queue.isEmpty())
		{
			long head=_queue.peek();
			
			if(head<now)
			{
				_queue.poll();
			}
			else
			{
				break;
			}
		}
	}
	
	public void clear()
	{
		if(_selfInited)
			_queue.clear();
		else
			_queue=null;
	}
	
	public int getNum()
	{
		return _queue.size();
	}
	
	public void add()
	{
		_queue.offer(_timeEntity.getTimeMillis()+_timeOut);
	}
	
	public void sub()
	{
		if(_queue.isEmpty())
		{
			Ctrl.warnLog("OperateNumTool,已为空，还调用sub");
			return;
		}
		
		_queue.poll();
	}
}
