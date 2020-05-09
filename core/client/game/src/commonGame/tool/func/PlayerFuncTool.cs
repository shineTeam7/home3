using System;
using ShineEngine;

/// <summary>
/// 角色功能插件基类
/// </summary>
public class PlayerFuncTool:FuncTool,IPlayerFuncTool
{
	/** 玩家自身 */
	public Player me;
	
	public PlayerFuncTool(int type,int funcID):base(type,funcID)
	{
	
	}

	/** 设置me */
	public void setMe(Player player)
	{
		me=player;
	}

	public virtual void afterReadDataSecond()
	{

	}
}