package com.home.shine.dataEx.watch;

/** 单个消息接收数据 */
public class NetMessageWatchData
{
	/** 消息号 */
	public int mid;
	/** 消息数目 */
	public int num;
	/** 总长 */
	public long allValue;
	
	/** 清空 */
	public void clear()
	{
		num=0;
		allValue=0L;
	}
	
	/** 添加一个 */
	public void addOne(int value)
	{
		++num;
		allValue+=value;
	}
}
