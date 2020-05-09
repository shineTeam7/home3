package com.home.shine.dataEx.watch;

import com.home.shine.support.collection.IntObjectMap;

/** 线程观测总数据 */
public class ThreadWatchAllData
{
	/** 是否生效中 */
	public boolean enabled=true;
	/** 观测索引 */
	public int index;
	/** 观测线程总数 */
	public int total;
	/** 当前回归数据 */
	public int num;
	/** 时间次数 */
	public int timeCount;
	/** 数据组 */
	private IntObjectMap<IntObjectMap<ThreadWatchOneData>> _datas=new IntObjectMap<>(IntObjectMap[]::new);
	
	/** 回归数据(观测线程) */
	public void addData(ThreadWatchOneData data)
	{
		//已失效
		if(!enabled)
			return;
		
		getOneDic(data.type).put(data.index,data);
		
		++num;
	}
	
	public IntObjectMap<ThreadWatchOneData> getOneDic(int type)
	{
		IntObjectMap<ThreadWatchOneData> dic=_datas.get(type);
		
		if(dic==null)
		{
			_datas.put(type,dic=new IntObjectMap<>(ThreadWatchOneData[]::new));
		}
		
		return dic;
	}
	
	/** 获取数据 */
	public ThreadWatchOneData getData(int type)
	{
		return getData(type,0);
	}
	
	/** 获取数据 */
	public ThreadWatchOneData getData(int type,int index)
	{
		IntObjectMap<ThreadWatchOneData> dic=_datas.get(type);
		
		if(dic==null)
			return null;
		
		return dic.get(index);
	}
}
