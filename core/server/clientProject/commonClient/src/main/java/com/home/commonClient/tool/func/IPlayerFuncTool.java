package com.home.commonClient.tool.func;

import com.home.commonClient.part.player.Player;

public interface IPlayerFuncTool
{
	void setMe(Player player);
	
	/** 收到事件 */
	void onEvent(int type,Object data);
	
	/** 游戏开始 */
	void onStart();
	
	/** 从库中读完数据后(做数据的补充解析)(onNewCreate后也会调用一次) */
	void afterReadDataSecond();
}
