package com.home.commonBase.control;

import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.thread.AbstractThread;
import com.home.shine.utils.MathUtils;

public class GameTestControl
{
	/** 测试线程被杀重启 */
	public static void testKillThread()
	{
		new Thread(()->
		{
			while(true)
			{
				try
				{
					Thread.sleep(MathUtils.randomRange(1000,2000));
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
				}
				
				AbstractThread ttt=ThreadControl.getThreadByType(MathUtils.randomRange2(1,8),MathUtils.randomRange(0,4));
				//Ctrl.print("杀一个",ttt.type,ttt.index);
				//强杀
				ttt.stop();
			}
		}).start();
	}
	
	public static void testMethod(Runnable func,int times)
	{
		int warmTimes=times*3;
		
		for(int i=0;i<warmTimes;++i)
		{
			func.run();
		}
		
		Ctrl.print("startTestMethod");
		
		long t=Ctrl.getTimer();
		
		for(int i=0;i<times;++i)
		{
			func.run();
		}
		
		Ctrl.print("endTestMethod",Ctrl.getTimer()-t);
	}
}
