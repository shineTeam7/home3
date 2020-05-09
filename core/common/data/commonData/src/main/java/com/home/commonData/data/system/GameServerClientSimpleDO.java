package com.home.commonData.data.system;

/** 游戏服给客户端的简版数据 */
public class GameServerClientSimpleDO
{
	/** ID */
	public int id;
	/** 客户端地址 */
	public String clientHost;
	/** 客户端使用端口(可能经过lbs等服务转发) */
	public int clientUsePort;
}
