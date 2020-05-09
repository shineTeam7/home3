package com.home.commonClient.part.player.base;

import com.home.commonBase.baseData.BasePart;
import com.home.commonClient.part.player.Player;
import com.home.shine.data.BaseData;

/** 角色部件基础 */
public abstract class PlayerBasePart extends BasePart
{
	/** 角色自身 */
	public Player me;
	
	/** 设置主 */
	public void setMe(Player player)
	{
		me=player;
	}
	
	/** 构造模块数据 */
	protected BaseData createPartData()
	{
		return null;
	}
	
	/** 构造数据前 */
	protected void beforeMakeData()
	{
	
	}
	
	/** 新创建时(为兼容服务器基类的该方法) */
	public void onNewCreate()
	{
		
	}
	
	/** 功能开启 */
	public abstract void onFunctionOpen(int id);
	
	/** 功能关闭 */
	public abstract void onFunctionClose(int id);
	
	/** 登出时 */
	public void onLeave()
	{
	
	}
	
	/** 开始时 */
	public void onStart()
	{
	
	}
	
	/** 升级 */
	public void onLevelUp(int oldLevel)
	{
	
	}
	
	/** 游戏消息 */
	public void onEvent(int type,Object data)
	{
	
	}
}
