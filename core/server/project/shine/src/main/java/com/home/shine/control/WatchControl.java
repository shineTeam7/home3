package com.home.shine.control;

import com.home.shine.constlist.NetWatchType;
import com.home.shine.constlist.ThreadType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.dataEx.watch.MainWatchData;
import com.home.shine.dataEx.watch.MessageIOWatchData;
import com.home.shine.dataEx.watch.NetMessageWatchData;
import com.home.shine.dataEx.watch.NetWatchData;
import com.home.shine.dataEx.watch.ThreadNetWatchData;
import com.home.shine.dataEx.watch.ThreadWatchAllData;
import com.home.shine.dataEx.watch.ThreadWatchOneData;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.request.PingRequest;
import com.home.shine.net.request.RePingRequest;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.SList;
import com.home.shine.tool.ThreadWatchTool;
import com.home.shine.utils.StringUtils;

import java.util.concurrent.atomic.AtomicLong;

/** 监控控制 */
public class WatchControl
{
	public static final int Watch_Logic=1;
	public static final int Watch_IO=2;
	
	/** 单例 */
	public static WatchControl instance;
	/** 网络线程观测组 */
	public static ThreadNetWatchData[] netThreadWatchs;
	/** 网络流量数据 */
	public static AtomicLong[] netFlows;
	/** 网络流量数据加头长 */
	public static AtomicLong[] netFlowsWithHead;
	
	/** 逻辑线程观测 */
	private static ThreadWatchTool _logicWatcher;
	/** io线程观测 */
	private static ThreadWatchTool _ioWatcher;
	
	private LogInfo _totalInfo=new LogInfo("total");
	private LogInfo _messageInfo=new LogInfo("message");
	
	public WatchControl()
	{
		//instance=this;
	}
	
	/** 初始化(shine调用) */
	public static void init()
	{
		netThreadWatchs=new ThreadNetWatchData[ShineSetting.ioThreadNum];
		for(int i=0;i<netThreadWatchs.length;i++)
		{
			netThreadWatchs[i]=new ThreadNetWatchData();
		}
		
		netFlows=new AtomicLong[NetWatchType.size];
		netFlowsWithHead=new AtomicLong[NetWatchType.size];
		
		for(int i=0;i<netFlows.length;i++)
		{
			netFlows[i]=new AtomicLong();
			netFlowsWithHead[i]=new AtomicLong();
		}
		
		_logicWatcher=new ThreadWatchTool(ShineSetting.logicThreadWatchDelay)
		{
			@Override
			protected void watchTimeOut(ThreadWatchAllData data)
			{
				if(data.getOneDic(ThreadType.Main).isEmpty())
				{
					Ctrl.print("观测时,主线程未返回");
				}
				
				IntObjectMap<ThreadWatchOneData> dic=data.getOneDic(ThreadType.Pool);
				
				if(dic.size()<ShineSetting.poolThreadNum)
				{
					for(int i=0;i<ShineSetting.poolThreadNum;i++)
					{
						if(dic.get(i)==null)
						{
							Ctrl.print("观测时,池线程:"+i+"未返回");
						}
					}
				}
				
				dic=data.getOneDic(ThreadType.IO);
				
				if(dic.size()<ShineSetting.ioThreadNum)
				{
					for(int i=0;i<ShineSetting.ioThreadNum;i++)
					{
						if(dic.get(i)==null)
						{
							Ctrl.print("观测时,IO线程:"+i+"未返回");
						}
					}
				}
			}
			
			@Override
			protected void watchOnce()
			{
				if(instance==null)
					return;
				
				ThreadWatchAllData allData=addWatchData();
				
				allData.total=ShineSetting.poolThreadNum+ShineSetting.ioThreadNum+1;
				
				addOneWatch(allData,ThreadType.Main,0);
				
				for(int i=ShineSetting.poolThreadNum-1;i>=0;--i)
				{
					addOneWatch(allData,ThreadType.Pool,i);
				}
				
				for(int i=ShineSetting.ioThreadNum-1;i>=0;--i)
				{
					addOneWatch(allData,ThreadType.IO,i);
				}
			}
			
			@Override
			protected ThreadWatchOneData createWatchData(int type,int index)
			{
				return WatchControl.instance.toCreateWatchData(type,index);
			}
			
			@Override
			protected void watchOnceOver(ThreadWatchAllData allData)
			{
				WatchControl.instance.watchLog(allData);
			}
		};
		
		_ioWatcher=new ThreadWatchTool(ShineSetting.ioThreadWatchDelay)
		{
			@Override
			protected void watchTimeOut(ThreadWatchAllData data)
			{
				IntObjectMap<ThreadWatchOneData> dic=data.getOneDic(ThreadType.IO);
				
				if(dic.size()<ShineSetting.ioThreadNum)
				{
					for(int i=0;i<ShineSetting.ioThreadNum;i++)
					{
						if(dic.get(i)==null)
						{
							Ctrl.print("观测IO时,IO线程:"+i+"未返回");
						}
					}
				}
			}
			
			@Override
			protected void watchOnce()
			{
				if(instance==null)
					return;
				
				ThreadWatchAllData allData=addWatchData();
				allData.total=ShineSetting.ioThreadNum;
				
				for(int i=ShineSetting.ioThreadNum-1;i>=0;--i)
				{
					addOneWatch(allData,ThreadType.IO,i);
				}
			}
			
			@Override
			protected ThreadWatchOneData createWatchData(int type,int index)
			{
				return new MessageIOWatchData();
			}
			
			@Override
			protected void watchOnceOver(ThreadWatchAllData allData)
			{
				WatchControl.instance.watchMessageIOLog(allData);
			}
		};
		
		
	}
	
	/** tick */
	public static void tick(int delay)
	{
		_logicWatcher.tick(delay);
		_ioWatcher.tick(delay);
	}
	
	/** 创建线程观测数据(对应线程) */
	public ThreadWatchOneData toCreateWatchData(int type,int index)
	{
		switch(type)
		{
			case ThreadType.Main:
				return new MainWatchData();
		}
		
		return new ThreadWatchOneData();
	}
	
	/** watch输出(观测线程) */
	public void watchLog(ThreadWatchAllData data)
	{
	
	}
	
	/** 写io线程watch输出 */
	protected void writeNetFlowWatchLog(LogInfo info)
	{
		if(!ShineSetting.needNetFlowWatch)
			return;
		
		AtomicLong al;
		AtomicLong al2;
		
		for(int i=0;i<NetWatchType.size;i++)
		{
			al=netFlows[i];
			al2=netFlowsWithHead[i];
			
			info.put(NetWatchType.getWatchName(i),StringUtils.toMBString(al.get()*1000/ShineSetting.logicThreadWatchDelay)+"/s");
			info.put(NetWatchType.getWatchName(i)+"WithHead",StringUtils.toMBString(al2.get()*1000/ShineSetting.logicThreadWatchDelay)+"/s");
			//归零
			al.set(0);
			al2.set(0);
		}
	}
	
	/** io线程watch输出 */
	public void watchMessageIOLog(ThreadWatchAllData data)
	{
		ThreadNetWatchData tData=((MessageIOWatchData)data.getData(ThreadType.IO,0)).data;
		
		for(int i=1;i<ShineSetting.ioThreadNum;++i)
		{
			tData.add(((MessageIOWatchData)data.getData(ThreadType.IO,i)).data);
		}
		
		LogInfo info=_totalInfo;
		
		for(int i=0;i<NetWatchType.size;i++)
		{
			info.put(NetWatchType.getWatchName(i),StringUtils.toMBString(tData.datas[i].allValue));
		}
		
		LogControl.messageLog(info.getStringAndClearToHead());
		
		SList<NetMessageWatchData> list=new SList<>(NetMessageWatchData[]::new);
		
		for(int i=0;i<NetWatchType.size;i++)
		{
			writeOneMessage(list,i,tData.datas[i]);
		}
	}
	
	private void writeOneMessage(SList<NetMessageWatchData> list,int type,NetWatchData data)
	{
		NetMessageWatchData nData;
		int len;
		
		LogInfo info=_messageInfo;
		
		data.writeToList(list);
		
		len=Math.min(ShineSetting.netMessageCountMax,list.size());
		
		for(int i=0;i<len;i++)
		{
			nData=list.get(i);
			
			_messageInfo.put("type",NetWatchType.getWatchName(type));
			_messageInfo.put("mid",nData.mid);
			_messageInfo.put("name",getMessageDataName(type,nData.mid));
			_messageInfo.put("num",nData.num);
			_messageInfo.put("size",StringUtils.toMBString(nData.allValue));
			LogControl.messageLog(info.getStringAndClearToHead());
		}
		
		list.clear();
	}
	
	/** 获取某协议数据ID的名字 */
	private static String getMessageDataName(int type,int dataID)
	{
		switch(type)
		{
			case NetWatchType.ClientSend:
			case NetWatchType.ServerSend:
			{
				return BytesControl.getRequestName(dataID);
			}
			case NetWatchType.ClientReceive:
			case NetWatchType.ServerReceive:
			{
				return BytesControl.getResponseName(dataID);
			}
		}
		
		return "unknown";
	}
}
