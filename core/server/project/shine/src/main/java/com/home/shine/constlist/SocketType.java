package com.home.shine.constlist;

import com.home.shine.ctrl.Ctrl;

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
	/** 场景服连接 */
	public static final int Scene=6;
	/** 管理服连接 */
	public static final int Manager=7;
	/** gm服连接 */
	public static final int GMClient=8;
	/** 客户端发送连接(机器人) */
	public static final int ClientSend=9;
	
	/** 尺寸 */
	public static int size=10;
	
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
			case Scene:
				return "场景服连接";
			case Manager:
				return "管理服连接";
			case GMClient:
				return "gm服连接";
			case ClientSend:
				return "客户端发送连接";
			default:
			{
				Ctrl.throwError("未注册的连接名",type);
				return "";
			}
		}
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
			case Scene:
				return "scene_"+sendID;
			case Manager:
				return "manager_"+sendID;
		}
		
		return "";
	}
}
