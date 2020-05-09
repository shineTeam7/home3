using System;
using ShineEngine;

/// <summary>
/// trigger间隔数据
/// </summary>
public class TriggerIntervalData:IPoolObject
{
	/** 实例 */
	public TriggerInstance instance;
	/** 间隔 */
	public int delay;
	/** 当前值 */
	public int current;

	public void clear()
	{
		instance=null;
		delay=0;
		current=0;
	}
}