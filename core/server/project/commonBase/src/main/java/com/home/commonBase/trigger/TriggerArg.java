package com.home.commonBase.trigger;

import com.home.shine.support.pool.IPoolObject;

public class TriggerArg implements IPoolObject
{
	public TriggerInstance instance;
	
	public TriggerEvent evt;
	/** 当前runner */
	public TriggerActionRunner runner;
	/** 父项runner(trigger层用) */
	public TriggerActionRunner parentRunner;
	
	@Override
	public void clear()
	{
		instance=null;
		evt=null;
		runner=null;
		parentRunner=null;
	}
}
