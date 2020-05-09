using System;
using ShineEngine;

/// <summary>
/// trigger实例
/// </summary>
public class TriggerInstance:IPoolObject
{
	/** 配置 */
	public TriggerConfigData config;
	/** 间隔组 */
	public SSet<TriggerIntervalData> intervalDic=new SSet<TriggerIntervalData>();

	public void clear()
	{
		config=null;
	}
}