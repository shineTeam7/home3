package com.home.shine.dataEx.watch;

import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;

import java.util.Comparator;

/** 网络观测数据 */
public class NetWatchData
{
	/** 总长度 */
	public long allValue=0L;
	
	/** 消息组 */
	public IntObjectMap<NetMessageWatchData> dic=new IntObjectMap<>(NetMessageWatchData[]::new);
	/** 比较方法 */
	private static Comparator<NetMessageWatchData> _compare=NetWatchData::compare;
	
	/** 添加数据 */
	public void addOne(int mid,int value)
	{
		allValue+=value;
		getOneData(mid).addOne(value);
	}
	
	private NetMessageWatchData getOneData(int mid)
	{
		NetMessageWatchData data;
		if((data=dic.get(mid))==null)
		{
			data=new NetMessageWatchData();
			data.mid=mid;
			dic.put(mid,data);
		}
		
		return data;
	}
	
	/** 清空(dic部分) */
	public void clear()
	{
		allValue=0L;
		
		dic.forEachValue(v->
		{
			v.clear();
		});
	}
	
	/** 增加 */
	public void add(NetWatchData data)
	{
		allValue+=data.allValue;
		
		data.dic.forEachValue(v->
		{
			if(v.num>0)
			{
				NetMessageWatchData v2=getOneData(v.mid);
				v2.num+=v.num;
				v2.allValue+=v.allValue;
			}
		});
	}
	
	/** 写到list中 */
	public void writeToList(SList<NetMessageWatchData> list)
	{
		list.clear();
		
		dic.forEachValue(v->
		{
			list.add(v);
		});
		
		list.sort(_compare);
	}
	
	private static int compare(NetMessageWatchData arg0,NetMessageWatchData arg1)
	{
		if(arg0.allValue>arg1.allValue)
			return -1;
		
		if(arg0.allValue<arg1.allValue)
			return 1;
		
		return 0;
	}
}
