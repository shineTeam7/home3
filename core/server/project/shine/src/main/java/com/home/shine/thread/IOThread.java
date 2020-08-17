package com.home.shine.thread;

import com.home.shine.constlist.ThreadType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.http.HttpReceive;
import com.home.shine.net.httpRequest.BaseHttpRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.concurrent.collection.NatureConcurrentQueue;

/** IO线程(采用SThread) */
//默认先采用CThread
public class IOThread extends CThread
//public class IOThread extends SThread
{
	private SSet<BaseSocket> _socketDic=new SSet<>(BaseSocket[]::new);
	private SSet<HttpReceive> _httpReceiveDic=new SSet<>(HttpReceive[]::new);
	
	private SSet<BaseHttpRequest> _httpRequestDic=new SSet<>(BaseHttpRequest[]::new);
	
	private int _checkHttpTimePass=0;
	
	//private int _checkHttpTimePass=0;
	
	public IOThread(int index)
	{
		super("ioThread-"+index,ThreadType.IO,index);
	}
	
	@Override
	protected void tick(int delay)
	{
		super.tick(delay);
		
		SSet<BaseSocket> set;
		
		if(!(set=_socketDic).isEmpty())
		{
			BaseSocket[] keys=set.getKeys();
			BaseSocket socket;
			for(int i=keys.length - 1;i >= 0;--i)
			{
				if((socket=keys[i])!=null)
				{
					socket.onFrame(delay);
				}
			}
		}
		
		if((_checkHttpTimePass+=delay)>ShineSetting.httpRequestCheckDelay)
		{
			_checkHttpTimePass=0;
			
			_socketDic.tryShrink();
			
			checkHttpReceive(ShineSetting.httpRequestCheckDelay);
			checkHttpRequest(ShineSetting.httpRequestCheckDelay);
		}
	}
	
	private void checkHttpReceive(int delay)
	{
		if(_httpReceiveDic.isEmpty())
			return;
		
		_httpReceiveDic.forEachS(v->
		{
			if((v.timeOut-=delay)<=0)
			{
				v.disposeForIO();
			}
		});
	}
	
	private void checkHttpRequest(int deley)
	{
		if(_httpRequestDic.isEmpty())
			return;
		
		_httpRequestDic.forEachS(v->
		{
			if(v.removed)
			{
				v.dispose();
				_httpRequestDic.remove(v);
			}
			else if((v.timeOut-=deley)<=0)
			{
				v.onTimeOut();
				_httpRequestDic.remove(v);
			}
		});
	}
	
	//@Override
	//public void addFunc(Runnable func)
	//{
	//	if(ShineSetting.openCheck)
	//	{
	//		if(!(Thread.currentThread() instanceof AbstractThread))
	//		{
	//			Ctrl.throwError("不是自己的线程");
	//		}
	//	}
	//
	//	super.addFunc(func);
	//}
	
	/** 添加其他线程执行 */
	public void addOtherFunc(Runnable func)
	{
		if(func==null)
			return;
		
		toAddFunc(func,null);
		
		if(_needNotify)
			notifyFunc();
	}
	
	/** 注册socket(IO线程调用) */
	public void addSocket(BaseSocket socket)
	{
		_socketDic.add(socket);
	}
	
	/** 删除socket(IO线程调用) */
	public void removeSocket(BaseSocket socket)
	{
		_socketDic.remove(socket);
	}
	
	/** 添加httpReceive(IO线程调用) */
	public void addHttpReceive(HttpReceive receive)
	{
		if(ShineSetting.openCheck)
		{
			if(Thread.currentThread()!=this)
			{
				Ctrl.errorLog("addHttpReceive不是当前线程",this.index,Thread.currentThread().getName());
			}
		}
		
		_httpReceiveDic.add(receive);
	}
	
	/** 移除httpReceive(IO线程调用) */
	public void removeHttpReceive(HttpReceive receive)
	{
		if(ShineSetting.openCheck)
		{
			if(Thread.currentThread()!=this)
			{
				Ctrl.errorLog("addHttpReceive不是当前线程",this.index,Thread.currentThread().getName());
			}
		}
		
		_httpReceiveDic.remove(receive);
	}
	
	/** 添加httpRequest(IO线程调用) */
	public void addHttpRequest(BaseHttpRequest request)
	{
		_httpRequestDic.add(request);
	}
	
	@Override
	public void copy(AbstractThread thread)
	{
		super.copy(thread);
		
		IOThread thd=(IOThread)thread;
		
		_socketDic=thd._socketDic;
		_httpReceiveDic=thd._httpReceiveDic;
		_httpRequestDic=thd._httpRequestDic;
	}
}
