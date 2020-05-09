package com.home.commonClient.dataEx;

import com.home.commonClient.control.LogicExecutor;
import com.home.commonClient.global.ClientC;
import com.home.shine.dataEx.watch.ThreadWatchOneData;

public class ClientPoolWatchData extends ThreadWatchOneData
{
	/** 玩家数 */
	public int playerNum;
	
	@Override
	public void make()
	{
		super.make();
		
		LogicExecutor logic=ClientC.main.getExecutor(index);
		
		playerNum=logic.getPlayerNum();
	}
}
