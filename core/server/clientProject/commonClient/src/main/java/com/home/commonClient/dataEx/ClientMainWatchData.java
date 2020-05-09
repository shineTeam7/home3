package com.home.commonClient.dataEx;

import com.home.commonClient.global.ClientC;
import com.home.shine.dataEx.watch.MainWatchData;

public class ClientMainWatchData extends MainWatchData
{
	/** 玩家在线人数 */
	public int playerOnlineNum;
	
	@Override
	public void make()
	{
		super.make();
		
		playerOnlineNum=ClientC.main.getPlayerNum();
	}
}
