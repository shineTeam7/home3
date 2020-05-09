package com.home.commonGame.dataEx;

import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.shine.dataEx.watch.ThreadWatchOneData;

/** Game池线程观测数据 */
public class GamePoolWatchData extends ThreadWatchOneData
{
	/** 玩家数 */
	public int playerNum;
	/** 存在场景数 */
	public int sceneNum;
	
	@Override
	public void make()
	{
		super.make();
		
		LogicExecutor logic=GameC.main.getExecutor(index);
		
		playerNum=logic.getPlayerNum();
		sceneNum=logic.getSceneNum();
	}
}
