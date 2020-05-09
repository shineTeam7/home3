package com.home.shine.dataEx.watch;

import com.home.shine.ctrl.Ctrl;

/** 主线程观测数据 */
public class MainWatchData extends ThreadWatchOneData
{
	/** 时间戳 */
	public long time;
	/** 当前内存 */
	public String useMemory;
	/** 总内存 */
	public String totalMemory;
	
	@Override
	public void make()
	{
		super.make();
		
		time=Ctrl.getTimer();
		useMemory=Ctrl.getMemoryStr();
		totalMemory=Ctrl.getTotalMemoryStr();
	}
}
