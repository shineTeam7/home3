package com.home.shine.support;

import com.home.shine.control.ThreadControl;
import com.home.shine.data.BaseData;
import com.home.shine.support.pool.DataPool;
import com.home.shine.thread.AbstractThread;

/** 线程数据缓存 */
public class ThreadDataCache
{
	private BaseData[] _threadCacheArr;
	
	private boolean _isEmpty=false;
	
	public void init()
	{
		_threadCacheArr=new BaseData[ThreadControl.threadLength];
	}
	
	public boolean isEmpty()
	{
		return _isEmpty;
	}
	
	/** 暂时缓存数据 */
	public void cacheData(BaseData data)
	{
		byte index;
		if((index=data.createThreadInstance)==-1)
			return;
		
		_isEmpty=false;
		
		BaseData head=_threadCacheArr[index];
		
		if(head!=null)
		{
			data.releaseLinkTail=head;
		}
		
		_threadCacheArr[index]=data;
	}
	
	/** 将缓存归还各个线程 */
	public void flushCache()
	{
		if(_isEmpty)
			return;
		
		BaseData[] arr;
		BaseData head;
		
		for(int i=(arr=_threadCacheArr).length-1;i>=0;--i)
		{
			if((head=arr[i])!=null)
			{
				arr[i]=null;
				sendHead(i,head);
			}
		}
		
		_isEmpty=true;
	}
	
	private void sendHead(int instance,BaseData data)
	{
		AbstractThread thread=ThreadControl.getThreadByInstance(instance);
		DataPool pool=thread.pool;
		
		thread.addFunc(()->
		{
			BaseData current=data;
			BaseData next;
			
			while(current!=null)
			{
				next=current.releaseLinkTail;
				current.releaseLinkTail=null;
				pool.releaseData(current);
				current=next;
			}
		});
	}
}
