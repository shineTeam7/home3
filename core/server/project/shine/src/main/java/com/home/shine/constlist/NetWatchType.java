package com.home.shine.constlist;

/** 网络观测类型 */
public class NetWatchType
{
	/** 客户端发送 */
	public static final int ClientSend=0;
	/** 客户端接收 */
	public static final int ClientReceive=1;
	/** 服务器发送 */
	public static final int ServerSend=2;
	/** 服务器接收 */
	public static final int ServerReceive=3;
	
	/** 尺寸 */
	public static int size=4;
	
	/** 获取观测名字 */
	public static String getWatchName(int type)
	{
		switch(type)
		{
			case ClientSend:
				return "clientSend";
			case ClientReceive:
				return "clientReceive";
			case ServerSend:
				return "serverSend";
			case ServerReceive:
				return "serverReceive";
		}
		
		return "";
	}
}
