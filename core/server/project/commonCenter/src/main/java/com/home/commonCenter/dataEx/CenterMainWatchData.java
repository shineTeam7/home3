package com.home.commonCenter.dataEx;

import com.home.commonCenter.global.CenterC;
import com.home.shine.dataEx.watch.MainWatchData;

public class CenterMainWatchData extends MainWatchData
{
	/** 玩家在线人数 */
	public int playerOnlineNum;
	
	@Override
	public void make()
	{
		super.make();
		
		playerOnlineNum=CenterC.main.getPlayerOnlineNum();
	}
}
