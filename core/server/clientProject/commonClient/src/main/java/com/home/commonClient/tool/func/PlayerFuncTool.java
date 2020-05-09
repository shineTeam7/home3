package com.home.commonClient.tool.func;

import com.home.commonBase.tool.func.FuncTool;
import com.home.commonClient.part.player.Player;

/** 角色功能插件基类 */
public class PlayerFuncTool extends FuncTool implements IPlayerFuncTool
{
	/** 玩家自身 */
	public Player me;
	
	public PlayerFuncTool(int type,int funcID)
	{
		super(type,funcID);
	}
	
	/** 设置me */
	public void setMe(Player player)
	{
		me=player;
	}
	
	@Override
	public void onEvent(int type,Object data)
	{
	
	}
	
	@Override
	public void onStart()
	{
	
	}
	
	@Override
	public void afterReadDataSecond()
	{
	
	}
}
