package com.home.shineTest.mtest;

import com.home.shineTest.thread.IThread;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/** 线程性能测试 */
public class MThreadTest
{
	/** 线程数 */
	private static int _threadNum=8;
	/** 每个线程的方法数 */
	private static int _funcNum=10000;
	/** 轮数 */
	private static int _roundNum=10;
	
	private int[] _records;
	
	private MainThread _mainThread=new MainThread();
	
	private IThread _thread;
	
	private AtomicInteger _countRecord=new AtomicInteger();
	
	private volatile long _startTime;
	
	//次数
	
	private long _timeTotal=0;
	
	private int _roundCount=0;
	
	//private ScheduledFuture<?> _future;
	
	//round
	
	private int _warmupNum;
	
	private boolean _isWarmUp;
	
	public MThreadTest()
	{
		_records=new int[_threadNum];
		
		_thread=new MThread();
		//_thread=new TThread();
		_thread.start();
	}
	
	/** 开始测试 */
	public void start()
	{
		_mainThread.start();
		
		_mainThread.addFunc(()->
		{
			testRound();
		});
	}
	
	private void testRound()
	{
		_roundCount=0;
		_timeTotal=0;
		
		//_future=Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(this::testOnce,0,500,TimeUnit.MILLISECONDS);
		
		testOnce();
	}
	
	/** 测试一次 */
	protected void testOnce()
	{
		_countRecord.set(0);
		
		_startTime=System.currentTimeMillis();
		
		for(int i=0;i<_threadNum;i++)
		{
			new ProducerThread(i).start();
		}
	}
	
	protected void testOnceOver()
	{
		_countRecord.set(0);
		
		for(int i=0;i<_threadNum;i++)
		{
			_records[i]=0;
		}
		
		long delay=System.currentTimeMillis() - _startTime;
		
		_timeTotal+=delay;
		
		System.out.println("一次时间"+delay);
		
		//够了
		if(++_roundCount==_roundNum)
		{
			//_future.cancel(false);
			
			System.out.println("平均时间"+(_timeTotal/_roundNum));
			
			roundOver();
		}
		else
		{
			testOnce();
		}
	}
	
	private void roundOver()
	{
		if(++_warmupNum==2)
		{
			//结束
			System.out.println("结束");
		}
		else
		{
			//再次
			testRound();
		}
	}
	
	private class MainThread extends Thread implements IThread
	{
		private ConcurrentLinkedDeque<Runnable> _queue=new ConcurrentLinkedDeque<>();
		
		@Override
		public void run()
		{
			while(true)
			{
				Runnable func=_queue.poll();
				
				if(func!=null)
				{
					func.run();
				}
				else
				{
					LockSupport.parkNanos(1L);
				}
			}
		}
		
		@Override
		public void addFunc(Runnable func)
		{
			_queue.offer(func);
		}
	}
	
	private class ProducerThread extends Thread
	{
		private int _index;
		
		public ProducerThread(int index)
		{
			_index=index;
		}
		
		@Override
		public void run()
		{
			//方法次数
			for(int i=_funcNum-1;i>=0;--i)
			{
				_thread.addFunc(()->
				{
					//够了
					if(++_records[_index]>=_funcNum)
					{
						//够了
						if(_countRecord.incrementAndGet()==_threadNum)
						{
							_mainThread.addFunc(()->
							{
								testOnceOver();
							});
						}
					}
				});
			}
		}
	}
}
