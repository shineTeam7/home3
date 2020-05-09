package com.home.commonBase.trigger;

import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.data.trigger.TriggerFuncData;
import com.home.shine.support.pool.IPoolObject;

/** trigger event注册数据 */
public class TriggerEventRegistData implements IPoolObject
{
	/** 配置 */
	public TriggerConfigData config;
	/** 序号 */
	public int index;
	/** event函数 */
	public TriggerFuncData data;
	/** 优先级 */
	public int priority;
	/** 是否使用中 */
	public boolean isUsing=false;
	/** 是否需要析构 */
	public boolean needRelease=false;
	
	@Override
	public void clear()
	{
		config=null;
		data=null;
		isUsing=false;
		needRelease=false;
	}
}
