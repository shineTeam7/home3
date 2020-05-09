package com.home.commonGame.tool.func;

import com.home.commonGame.part.player.Player;

/** 玩家功能插件接口 */
public interface IPlayerFuncTool
{
	/** 设置玩家 */
	void setMe(Player player);
	
	/** 登录前(主线程)(beforeLogin在beforeEnter前) */
	void beforeLoginOnMain();
	
	/** 进入前(主线程)(beforeLogin在beforeEnter前 */
	void beforeEnterOnMain();
	
	/** 登录前(逻辑线程) */
	void beforeLogin();
	
	/** 统计该模块的战斗力 */
	long countFightForce();
}
