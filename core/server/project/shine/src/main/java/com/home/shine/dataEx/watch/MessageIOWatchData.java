package com.home.shine.dataEx.watch;

import com.home.shine.control.WatchControl;

/** 消息IO线程观测数据 */
public class MessageIOWatchData extends ThreadWatchOneData
{
	/** 观测数据 */
	public ThreadNetWatchData data;
	
	@Override
	public void make()
	{
		super.make();
		
		ThreadNetWatchData tData=WatchControl.netThreadWatchs[index];
		
		//clone
		data=tData.clone();
		
		tData.clear();
	}
}
