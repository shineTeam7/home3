package com.home.shine.constlist;

/** udp执行类型 */
public class UDPCommandType
{
	/** 发包 */
	public static final int Pack=1;
	/** 请求包 */
	public static final int RequestPack=2;
	/** 出错(包等待超上限)(被动关闭) */
	public static final int Error=3;
	/** 主动关闭 */
	public static final int Close=4;
	/** 连接 */
	public static final int Connect=5;
	/** 连接成功 */
	public static final int ConnectSuccess=6;
}
