package com.home.shine.constlist;

/** 连接类型 */
public class SocketType
{
	/** 客户端接收连接 */
	public static final int ClientReceive=1;
	/** 服务器接收连接 */
	public static final int ServerReceive=2;
	/** 中心服连接 */
	public static final int Center=3;
	/** 游戏服连接 */
	public static final int Game=4;
	/** 登陆服连接 */
	public static final int Login=5;
	/** 管理服连接 */
	public static final int Manager=6;
	/** gm服连接 */
	public static final int GMClient=7;
	/** 客户端发送连接(机器人) */
	public static final int ClientSend=8;
	
	/** 尺寸 */
	public static int size=9;
	
	public static String getName(int type)
	{
		switch(type)
		{
			case ClientReceive:
				return "客户端接收连接";
			case ServerReceive:
				return "服务器接收连接";
			case Center:
				return "中心服连接";
			case Game:
				return "游戏服连接";
			case Login:
				return "登陆服连接";
			case Manager:
				return "管理服连接";
			case GMClient:
				return "gm服连接";
			case ClientSend:
				return "客户端发送连接";
		}
		
		return "";
	}
	
	public static String getQName(int type,int sendID)
	{
		switch(type)
		{
			case Center:
				return "center_"+sendID;
			case Game:
				return "game_"+sendID;
			case Login:
				return "login_"+sendID;
			case Manager:
				return "manager_"+sendID;
		}
		
		return "";
	}
}
