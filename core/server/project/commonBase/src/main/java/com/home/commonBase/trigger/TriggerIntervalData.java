package com.home.commonBase.trigger;

import com.home.shine.support.pool.IPoolObject;

/** trigger间隔数据 */
public class TriggerIntervalData implements IPoolObject
{
	/** 实例 */
	public TriggerInstance instance;
	/** 间隔 */
	public int delay;
	/** 当前值 */
	public int current;
	
	@Override
	public void clear()
	{
		instance=null;
		delay=0;
		current=0;
	}
}
