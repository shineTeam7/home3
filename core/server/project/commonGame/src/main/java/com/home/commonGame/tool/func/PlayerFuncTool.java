package com.home.commonGame.tool.func;

import com.home.commonBase.tool.func.FuncTool;
import com.home.commonGame.part.player.Player;

public class PlayerFuncTool extends FuncTool implements IPlayerFuncTool
{
	/** 角色 */
	public Player me;
	
	public PlayerFuncTool(int type,int funcID)
	{
		super(type,funcID);
	}
	
	/** 设置主角 */
	public void setMe(Player player)
	{
		me=player;
	}
	
	/** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次) */
	public void afterReadDataSecond()
	{
	
	}
	
	/** 登录前(主线程) */
	public void beforeLoginOnMain()
	{
	
	}
	
	/** 进入前(主线程) */
	public void beforeEnterOnMain()
	{
	
	}
	
	/** 登录前(逻辑线程) */
	public void beforeLogin()
	{
	
	}
	
	/** 统计该模块的战斗力 */
	public long countFightForce()
	{
		return 0L;
	}
}
