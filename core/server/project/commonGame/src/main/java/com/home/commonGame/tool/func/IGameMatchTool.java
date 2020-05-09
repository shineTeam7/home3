package com.home.commonGame.tool.func;

import com.home.commonGame.part.player.Player;

/** game匹配插件接口 */
public interface IGameMatchTool
{
	/** 检查是否可匹配(逻辑线程) */
	boolean checkCanMatch(Player player);
	/** 添加角色(主线程) */
	boolean addPlayer(Player player);
	/** 取消匹配(主线程) */
	boolean cancelMatch(long playerID);
	/** 接受匹配 */
	void acceptMatch(int index,long playerID);
}
