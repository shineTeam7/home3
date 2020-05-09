package com.home.commonGame.part.gameGlobal.base;

import com.home.commonBase.baseData.BasePart;
import com.home.commonBase.data.login.GameLoginData;
import com.home.commonBase.data.login.GameLoginToCenterData;
import com.home.commonBase.data.login.GameLoginToGameData;
import com.home.commonGame.logic.func.RoleGroup;
import com.home.commonGame.part.gameGlobal.GameGlobal;
import com.home.commonGame.part.player.Player;

/** 逻辑服global基类 */
public abstract class GameGlobalBasePart extends BasePart
{
	/** global自己 */
	public GameGlobal me;
	
	/** 设置主 */
	public void setMe(GameGlobal player)
	{
		me=player;
	}
	
	/** 开始运行时 */
	public void onStart()
	{
	
	}
	
	/** 读登录数据 */
	public void readCenterLoginData(GameLoginData data,boolean isInit)
	{
	
	}
	
	/** 构造登录到中心服数据 */
	public void makeLoginCenterData(GameLoginToCenterData data)
	{
	
	}
	
	/** 构造登录到逻辑服数据 */
	public void makeLoginGameData(GameLoginToGameData data)
	{
	
	}
	
	/** 读逻辑服登陆数据 */
	public void readGameLoginData(int gameID,GameLoginToGameData data)
	{
	
	}
	
	/** 角色进入(主线程) */
	public void onPlayerEnter(Player player)
	{
	
	}
	
	/** 角色退出(主线程) */
	public void onPlayerLeave(Player player)
	{
	
	}
	
	/** 角色创建时(主线程) */
	public void onPlayerCreate(Player player)
	{
	
	}
	
	/** 角色删除(主线程) */
	public void onPlayerDelete(Player player)
	{
	
	}
	
	/** 玩家群删除(主线程) */
	public void onRoleGroupDelete(RoleGroup roleGroup)
	{
	
	}
}
