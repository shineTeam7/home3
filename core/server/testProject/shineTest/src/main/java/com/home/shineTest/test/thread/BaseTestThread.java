package com.home.shineTest.test.thread;

import java.util.concurrent.atomic.AtomicInteger;

import com.home.shineTest.thread.IThread;
import com.home.shineTest.thread.TCThread;
import com.home.shine.ShineSetup;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;

public class BaseTestThread
{
	protected IThread _thread;
	
	private int _threadNum=1;
	protected int _waveNum=1000000;
	
	
	private long _startTime;
	private int[] _countDic;
	private AtomicInteger _atomic=new AtomicInteger();
	
	private int _count=0;
	
	private volatile long _timePass=0;
	
	private int _aa;
	
	public void testThread()
	{
		ShineSetup.setup();
		
		_countDic=new int[_threadNum];
		
		initThread();
		
		if(_thread!=null)
		{
			_thread.start();
		}
		
		_aa=ThreadControl.getMainTimeDriver().setInterval(this::testOnce,500);
	}
	
	protected void initThread()
	{
		_thread=new TCThread("thread");
	}
	
	/** 主线程 */
	private void testOnce(int delay)
	{
		//开始时间
		_startTime=Ctrl.getNanoTimer();
		
		for(int i=_countDic.length-1;i>=0;--i)
		{
			_countDic[i]=0;
		}
		
		_atomic.set(0);
		
		Ctrl.print("一次",_count);
		
		
		for(int i=0;i<_threadNum;++i)
		{
			testOnceNext(i);
		}
	}
	
	private void testOnceNext(int index)
	{
		new Thread(()->{toTestOnce(index);}).start();
	}
	
	protected void toTestOnce(int index)
	{
		int len=_waveNum;
		
		IThread thread=_thread;
		
		Runnable run=()->
		{
			this.execute(index);
		};
		
		for(int i=0;i<len;++i)
		{
			thread.addFunc(run);
		}
	}
	
	protected void execute(int index)
	{
		//够了
		if((++_countDic[index])==_waveNum)
		{
			testOver(index);
		}
	}
	
	private void testOver(int index)
	{
		//够了
		if(_atomic.incrementAndGet()==_threadNum)
		{
			_timePass+=Ctrl.getNanoTimer()-_startTime;
			
			if((++_count)==11)
			{
				Ctrl.print("结束",_timePass/10);
				
				ThreadControl.addMainFunc(()->
				{
					ThreadControl.getMainTimeDriver().clearInterval(_aa);
				});
				
				return;
			}
		}
	}
}
