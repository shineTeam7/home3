package com.home.shine.support.concurrent.collection;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

public class BaseSPSCQueue extends BaseConcurrentQueue
{
	private boolean _openCheck=ShineSetting.openCheck;
	
	private Thread _pThread;
	
	@Override
	public void addFunc(Runnable func)
	{
		if(_openCheck)
		{
			if(_pThread==null)
			{
				_pThread=Thread.currentThread();
			}
			else
			{
				if(_pThread!=Thread.currentThread())
				{
					Ctrl.throwError("不是单一生产者");
				}
			}
		}
	}
	
	/** 设置是否需要检测 */
	public void setOpenCheck(boolean value)
	{
		_openCheck=value;
	}
}
