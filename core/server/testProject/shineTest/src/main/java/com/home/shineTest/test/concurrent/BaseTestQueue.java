package com.home.shineTest.test.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import com.home.shine.ShineSetup;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.inter.IQueue;
import com.home.shine.thread.CThread;

/** 队列测试 */
public class BaseTestQueue
{
	protected int _pNum=4;
	
	protected int _cNum=4;
	
	private int _waveNum=100000;
	
	private CThread[] _producers;
	
	private Thread[] _consumers;
	
	private int _timeIndex;
	
	protected IQueue<Runnable> _queue;
	
	private AtomicInteger _atomic=new AtomicInteger();
	
	private volatile int _times=0;
	
	private volatile long _tt;
	
	private volatile long _timePass=0;
	
	public void testQueue()
	{
		ShineSetup.setup();
		
		initQueue();
		
		_producers=new CThread[_pNum];
		
		for(int i=0;i<_pNum;++i)
		{
			_producers[i]=new CThread("p"+i,ThreadType.Pool,i);
			_producers[i].start();
		}
		
		_consumers=new Thread[_cNum];
		
		for(int i=0;i<_cNum;++i)
		{
			_consumers[i]=new Thread(this::cRun);
			_consumers[i].start();
		}
		
		_timeIndex=ThreadControl.getMainTimeDriver().setInterval(this::testOnce,500);
	}
	
	protected void initQueue()
	{
//		_queue=new MPMCQueue<>(1024);
	}
	
//	private void pRun()
//	{
//		while(true)
//		{
//			try
//			{
//				Thread.sleep(100000);
//			}
//			catch(InterruptedException e)
//			{
////				e.printStackTrace();
//			}
//			
//			int len=_waveNum;
//			
//			for(int i=0;i<len;++i)
//			{
//				_queue.offer(this::eRun);
//			}
//		}
//	}
	
	private void cRun()
	{
		IQueue<Runnable> queue=_queue;
		
		while(true)
		{
			Runnable run=queue.poll();
			
			if(run!=null)
			{
				run.run();
			}
			
			Thread.yield();
		}
	}
	
	private void testOnce(int delay)
	{
		Ctrl.print("一次",_times);
		
		_atomic.set(0);
		
		_tt=Ctrl.getNanoTimer();
		
		for(int i=0;i<_pNum;++i)
		{
//			_producers[i].interrupt();
			_producers[i].addFunc(this::toTestOnce);
		}
	}
	
	protected void toTestOnce()
	{
		int len=_waveNum;
		
		IQueue<Runnable> queue=_queue;
		
		Runnable run=this::eRun;
		
		for(int i=0;i<len;++i)
		{
			queue.offer(run);
		}
	}
	
	private void eRun()
	{
		int re=_atomic.incrementAndGet();
		
		//够了
		if(re==_waveNum*_pNum)
		{
			testOver();
		}
	}
	
	private void testOver()
	{
		_times++;
		
		_timePass+=Ctrl.getNanoTimer()-_tt;
		
		if(_times==11)
		{
			Ctrl.print("结束",_timePass/10);
			
			ThreadControl.getMainTimeDriver().clearInterval(_timeIndex);
			
			return;
		}
	}
}
