package com.home.shineData.message;

/** 重连消息 */
public class SocketReconnectMO
{
	/** 源socketID */
	int socketID;
	/** 令牌 */
	int token;
	/** 最后收包索引 */
	int lastReceiveIndex;
}
