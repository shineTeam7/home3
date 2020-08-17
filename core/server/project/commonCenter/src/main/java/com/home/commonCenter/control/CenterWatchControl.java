package com.home.commonCenter.control;

import com.home.commonCenter.dataEx.CenterMainWatchData;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.dataEx.watch.ThreadWatchAllData;
import com.home.shine.dataEx.watch.ThreadWatchOneData;
import com.home.shine.global.ShineSetting;

/** center观测控制 */
public class CenterWatchControl extends WatchControl
{
	private LogInfo _info=new LogInfo("main");
	
	@Override
	public ThreadWatchOneData toCreateWatchData(int type,int index)
	{
		switch(type)
		{
			case ThreadType.Main:
			{
				return new CenterMainWatchData();
			}
		}
		
		return super.toCreateWatchData(type,index);
	}
	
	@Override
	public void watchLog(ThreadWatchAllData data)
	{
		LogInfo info=_info;
		
		CenterMainWatchData mData;
		
		if((mData=(CenterMainWatchData)data.getData(ThreadType.Main))!=null)
		{
			info.put("mainPer",mData.percent);
			info.put("mainFNum",mData.funcNum);
			info.put("useMemory",mData.useMemory);
			info.put("totalMemory",mData.totalMemory);
			info.put("playerOnlineNum",mData.playerOnlineNum);
		}
		else
		{
			info.put("main","null");
		}
		
		if(ShineSetting.needDetailLog)
		{
			//ThreadWatchOneData pData;
			//
			//for(int i=0;i<ShineSetting.poolThreadNum;++i)
			//{
			//	if((pData=data.getData(ThreadType.Pool,i))!=null)
			//	{
			//		info.put("lineFPS"+i,pData.fps);
			//		info.put("lineFuncNum"+i,pData.maxFuncNum);
			//	}
			//	else
			//	{
			//		info.put("line"+i,"null");
			//	}
			//}
			
			ThreadWatchOneData tData;
			
			for(int i=0;i<ShineSetting.ioThreadNum;++i)
			{
				if((tData=data.getData(ThreadType.IO,i))!=null)
				{
					info.put("ioPer"+i,tData.percent);
					info.put("ioFNum"+i,tData.funcNum);
				}
				else
				{
					info.put("io"+i,"null");
				}
			}
		}
		
		writeNetFlowWatchLog(info);
		
		Ctrl.runningLog(info.getStringAndClearToHead());
	}
}
