package com.home.commonGame.control;

import com.home.commonGame.dataEx.GameMainWatchData;
import com.home.commonGame.dataEx.GamePoolWatchData;
import com.home.shine.constlist.ThreadType;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.LogInfo;
import com.home.shine.dataEx.watch.ThreadWatchAllData;
import com.home.shine.dataEx.watch.ThreadWatchOneData;
import com.home.shine.global.ShineSetting;

/** game观测控制 */
public class GameWatchControl extends WatchControl
{
	private LogInfo _info=new LogInfo("main");
	
	@Override
	public ThreadWatchOneData toCreateWatchData(int type,int index)
	{
		switch(type)
		{
			case ThreadType.Main:
			{
				return new GameMainWatchData();
			}
			case ThreadType.Pool:
			{
				return new GamePoolWatchData();
			}
		}
		
		return super.toCreateWatchData(type,index);
	}
	
	@Override
	public void watchLog(ThreadWatchAllData data)
	{
		LogInfo info=_info;
		
		GameMainWatchData mData;
		
		if((mData=(GameMainWatchData)data.getData(ThreadType.Main))!=null)
		{
			info.put("mainFPS",mData.fps);
			info.put("mainFuncNum",mData.maxFuncNum);
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
			GamePoolWatchData pData;
			
			for(int i=0;i<ShineSetting.poolThreadNum;++i)
			{
				if((pData=(GamePoolWatchData)data.getData(ThreadType.Pool,i))!=null)
				{
					info.put("lineFPS"+i,pData.fps);
					info.put("lineFuncNum"+i,pData.maxFuncNum);
					info.put("linePlayerNum"+i,pData.playerNum);
					info.put("lineSceneNum"+i,pData.sceneNum);
				}
				else
				{
					info.put("line"+i,"null");
				}
			}
			
			ThreadWatchOneData tData;
			
			for(int i=0;i<ShineSetting.ioThreadNum;++i)
			{
				if((tData=data.getData(ThreadType.IO,i))!=null)
				{
					info.put("ioFPS"+i,tData.fps);
					info.put("ioFuncNum"+i,tData.maxFuncNum);
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
