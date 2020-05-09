package com.home.shineTest.test.thread;

import java.util.concurrent.atomic.AtomicInteger;

import com.home.shineTest.thread.TSThread;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.thread.AbstractThread;

public class TestThreadApp
{
	private AbstractThread _thd1;
	
	private AbstractThread _thd2;
	
	private int _a=0;
	
	private AtomicInteger _at=new AtomicInteger(0);
	
	public static void main(String[] args)
	{
		new TestThreadApp().test1();
		
		//-XX:FreqInlineSize=0
	}
	
	private void test1()
	{
		_thd1=new TSThread("thread1",512);
		_thd2=new TSThread("thread2",512);
		
		_thd1.start();
		_thd2.start();
		
		Thread thd=new Thread()
		{
			@Override
			public void run()
			{
				while(true)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch(InterruptedException e)
					{
						e.printStackTrace();
					}
					
//					Ctrl.print("看",_at.get(),_a);
				}
			}
		};
		
		thd.start();
		
//		_thd1.addFunc(this::func1);
		
		testSwitch();
	}
	
	private void func1()
	{
		if((_a & 1)!=0)
		{
			Ctrl.print("出错1");
		}
		
		_a++;
		
		_at.incrementAndGet();
		
		_thd2.addFunc(this::func2);
	}
	
	private void func2()
	{
		if((_a & 1)!=1)
		{
			Ctrl.print("出错2");
		}
		
//		_a++;
		_a--;
		
		_at.incrementAndGet();
		
		_thd1.addFunc(this::func1);
	}
	
	private Runnable _func1;
	
	private void testSwitch()
	{
		_a=0;
		
		Runnable func1=()->{
			
			if(_a!=0)
			{
				Ctrl.print("出错1");
			}
			
			_a++;
			
			_thd2.addFunc(()->{
				
				if(_a!=1)
				{
					Ctrl.print("出错2");
				}
				
				_a--;
				
				_thd1.addFunc(_func1);
			});
		};
		
		_func1=func1;
		
		_thd1.addFunc(func1);
	}
}
