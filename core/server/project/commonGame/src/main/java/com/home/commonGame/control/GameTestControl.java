package com.home.commonGame.control;

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
}
