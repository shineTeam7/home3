package com.home.commonBase.dataEx.scene;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SQueue;

/** 单位AOI统计数据 */
public class UnitAOICountData
{
	private static final int maxQueueSize=10;
	
	/** 消息栈 */
	private SQueue<Exception> _messageStack=new SQueue<>(Exception[]::new);
	/** 消息是否已有 */
	private boolean _hasMsg=false;
	/** 周围组栈 */
	private SQueue<Exception> _aroundStack=new SQueue<>(Exception[]::new);
	
	private boolean _hasAround=false;
	
	public void addMsg(boolean isAdd,Exception e)
	{
		if(isAdd)
		{
			if(_hasMsg)
			{
				Ctrl.throwError("AOI计数出错");
				return;
			}
			
			_hasMsg=true;
		}
		else
		{
			if(!_hasMsg)
			{
				Ctrl.throwError("AOI计数出错");
				return;
			}
			
			_hasMsg=false;
		}
		
		_messageStack.offer(e);
		
		if(_messageStack.size()>maxQueueSize)
			_messageStack.poll();
	}
	
	public void addAround(boolean isAdd,Exception e)
	{
		if(isAdd)
		{
			if(_hasAround)
			{
				Ctrl.throwError("AOI计数出错2");
				return;
			}
			
			_hasAround=true;
		}
		else
		{
			if(!_hasAround)
			{
				Ctrl.throwError("AOI计数出错2");
				return;
			}
			
			_hasAround=false;
		}
		
		_aroundStack.offer(e);
		
		if(_aroundStack.size()>maxQueueSize)
			_aroundStack.poll();
	}
	
	public void clearMsg()
	{
		_hasMsg=false;
		_messageStack.clear();
	}
}
