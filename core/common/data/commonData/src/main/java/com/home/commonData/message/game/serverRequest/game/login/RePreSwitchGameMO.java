package com.home.commonData.message.game.serverRequest.game.login;

/** 回复游戏服角色切换回消息 */
public class RePreSwitchGameMO
{
	/** 角色ID */
	long playerID;
	/** 令牌 */
	int token;
	
	/** 游戏服地址(因在每个game存储所有game服的地址端口成本更高,所以用每次推送实现) */
	String host;
	/** 游戏服端口 */
	int port;
}
