package com.home.commonGame.dataEx;

import com.home.commonGame.global.GameC;
import com.home.shine.dataEx.watch.MainWatchData;

/** game主线程观测数据 */
public class GameMainWatchData extends MainWatchData
{
	/** 玩家在线人数 */
	public int playerOnlineNum;
	
	@Override
	public void make()
	{
		super.make();
		
		playerOnlineNum=GameC.main.getPlayerOnlineNum();
		
	}
}
