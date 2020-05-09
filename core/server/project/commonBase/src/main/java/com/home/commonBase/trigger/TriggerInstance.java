package com.home.commonBase.trigger;

import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.support.collection.SSet;
import com.home.shine.support.pool.IPoolObject;

/** trigger实例 */
public class TriggerInstance implements IPoolObject
{
	/** 配置 */
	public TriggerConfigData config;
	/** 间隔组 */
	public SSet<TriggerIntervalData> intervalDic=new SSet<>(TriggerIntervalData[]::new);
	
	@Override
	public void clear()
	{
		config=null;
	}
}
