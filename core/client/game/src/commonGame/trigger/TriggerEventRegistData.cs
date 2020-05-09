using System;
using ShineEngine;

/// <summary>
/// trigger event注册数据
/// </summary>
public class TriggerEventRegistData:IPoolObject
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
	public bool isUsing=false;
	/** 是否需要析构 */
	public bool needRelease=false;

	public void clear()
	{
		config=null;
		data=null;
		isUsing=false;
		needRelease=false;
	}
}