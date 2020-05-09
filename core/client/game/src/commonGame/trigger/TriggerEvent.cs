using System;
using ShineEngine;

/// <summary>
/// trigger事件实例
/// </summary>
public class TriggerEvent:IPoolObject
{
	/** 类型 */
	public int type;

	public object[] args;

	/** 引用计数 */
	public int refCount;

	public void clear()
	{
		this.type=0;
		this.args=null;
	}

	/** 设置传入参数 */
	public void setArgs(int type,object[] args)
	{
		this.type=type;
		this.args=args;
	}
}