using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class TriggerArg:IPoolObject
{
	public TriggerInstance instance;

	public TriggerEvent evt;
	/** 当前runner */
	public TriggerActionRunner runner;
	/** 父项runner(trigger层用) */
	public TriggerActionRunner parentRunner;

	public void clear()
	{
		instance=null;
		evt=null;
		runner=null;
		parentRunner=null;
	}
}