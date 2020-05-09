package com.home.shine.tool;

import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.watch.ThreadWatchAllData;
import com.home.shine.dataEx.watch.ThreadWatchOneData;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.thread.AbstractThread;

/** 线程观测工具 */
public abstract class ThreadWatchTool
{
	private int _delay;
	/** 观测时间经过 */
	private int _watchTimePass=0;
	/** 观测索引 */
	private int _watchIndex=0;
	/** 观测数据缓存组 */
	private IntObjectMap<ThreadWatchAllData> _watchDatas=new IntObjectMap<>(ThreadWatchAllData[]::new);
	
	public ThreadWatchTool(int delay)
	{
		_delay=delay;
	}
	
	public void tick(int delay)
	{
		if((_watchTimePass+=delay)>=_delay)
		{
			_watchTimePass=0;//归零吧
			
			if(!_watchDatas.isEmpty())
			{
				ThreadWatchAllData[] values;
				ThreadWatchAllData v;
				
				for(int i=(values=_watchDatas.getValues()).length-1;i>=0;--i)
				{
					if((v=values[i])!=null)
					{
						//超时3次
						if((++v.timeCount)>=3)
						{
							//移除
							removeWatch(v.index);
							++i;
							
							Ctrl.warnLog("线程观测超时一次");
							
							watchTimeOut(v);
							//依旧回调
							watchOnceOver(v);
						}
					}
				}
			}
			
			watchOnce();
		}
	}
	
	/** 移除观测数据 */
	public void removeWatch(int index)
	{
		ThreadWatchAllData data=_watchDatas.remove(index);
		
		if(data==null)
			return;
		
		data.enabled=false;
	}
	
	abstract protected void watchTimeOut(ThreadWatchAllData data);
	
	/** 观测一次 */
	abstract protected void watchOnce();
	
	/** 添加观测数据 */
	public ThreadWatchAllData addWatchData()
	{
		ThreadWatchAllData allData=new ThreadWatchAllData();
		allData.index=++_watchIndex;
		_watchDatas.put(allData.index,allData);
		
		return allData;
	}
	
	/** 构造观测数据(对应线程) */
	private ThreadWatchOneData watchOne(int type,int index)
	{
		AbstractThread thread=ThreadControl.getThreadByType(type,index);
		
		ThreadWatchOneData data=createWatchData(type,index);
		data.type=type;
		data.index=index;
		data.maxFuncNum=thread.getMaxFuncNum();
		data.fps=thread.getCountFPS();
		
		thread.clearCount();
		
		data.make();
		return data;
	}
	
	protected ThreadWatchOneData createWatchData(int type,int index)
	{
		return new ThreadWatchOneData();
	}
	
	/** 添加一个观测 */
	public void addOneWatch(ThreadWatchAllData allData,int type,int index)
	{
		ThreadControl.addFuncByType(type,index,()->
		{
			ThreadWatchOneData wData=watchOne(type,index);
			
			ThreadControl.addWatchFunc(()->
			{
				allData.addData(wData);
				
				//够了
				if(allData.num==allData.total)
				{
					removeWatch(allData.index);
					
					watchOnceOver(allData);
				}
			});
		});
	}
	
	/** 一次观测结束 */
	abstract protected void watchOnceOver(ThreadWatchAllData allData);
}
