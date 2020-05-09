package com.home.commonBase.trigger;

import com.home.shine.support.pool.IPoolObject;

/** trigger事件实例 */
public class TriggerEvent implements IPoolObject
{
	/** 类型 */
	public int type;
	
	public Object[] args;
	
	/** 引用计数 */
	public int refCount;
	
	@Override
	public void clear()
	{
		this.type=0;
		this.args=null;
	}
	
	/** 设置传入参数 */
	public void setArgs(int type,Object[] args)
	{
		this.type=type;
		this.args=args;
	}
}
