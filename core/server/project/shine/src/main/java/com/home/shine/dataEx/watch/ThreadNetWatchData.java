package com.home.shine.dataEx.watch;

import com.home.shine.constlist.NetWatchType;

/** 线程网络观测数据 */
public class ThreadNetWatchData
{
	public NetWatchData[] datas;
	
	public ThreadNetWatchData()
	{
		datas=new NetWatchData[NetWatchType.size];
		for(int i=0;i<datas.length;i++)
		{
			datas[i]=new NetWatchData();
		}
	}
	
	/** 克隆 */
	public ThreadNetWatchData clone()
	{
		ThreadNetWatchData re=new ThreadNetWatchData();
		re.add(this);
		return re;
	}
	
	/** 清空 */
	public void clear()
	{
		for(int i=datas.length-1;i>=0;--i)
		{
			datas[i].clear();
		}
	}
	
	/** 添加 */
	public void add(ThreadNetWatchData data)
	{
		for(int i=datas.length-1;i>=0;--i)
		{
		    datas[i].add(data.datas[i]);
		}
	}
}
