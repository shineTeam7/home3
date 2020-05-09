package com.home.commonGame.dataEx;

import com.home.commonBase.data.login.PlayerLoginToEachGameData;
import com.home.commonBase.data.login.RePlayerLoginFromEachGameData;
import com.home.shine.support.collection.IntObjectMap;

/** 角色登陆其他逻辑服临时数据 */
public class PlayerLoginEachGameTempData
{
	/** 等待时间 */
	public int waitTime;
	
	public IntObjectMap<PlayerLoginToEachGameData> dic=new IntObjectMap<>(PlayerLoginToEachGameData[]::new);
	
	public RePlayerLoginFromEachGameData result;
	
	public void execute()
	{
	
	}
	
	
}
