package com.home.commonData.data.system;

import com.home.shineData.support.OnlyS;

/** 服务器启动信息 */
@OnlyS
public class ServerInfoDO
{
	/** ID */
	public int id;
	
	/** 服务器地址 */
	public String serverHost;
	
	/** 服务器端口 */
	public int serverPort;
	
	/** 服务器http端口 */
	public int serverHttpPort;
	
	/** 客户端地址 */
	public String clientHost;
	
	/** 客户端端口 */
	public int clientPort;
	
	/** 客户端http端口 */
	public int clientHttpPort;
	
	/** mysql */
	public String mysql;
}
