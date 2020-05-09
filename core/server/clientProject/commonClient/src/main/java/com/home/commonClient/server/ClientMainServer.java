package com.home.commonClient.server;

import com.home.commonClient.global.ClientGlobal;
import com.home.commonClient.tool.ClientMainHttpResponseMaker;
import com.home.shine.server.BaseServer;

/** 客户端主服务 */
public class ClientMainServer extends BaseServer
{
	@Override
	public void init()
	{
		super.init();
		
		setClientHttpResponseMaker(new ClientMainHttpResponseMaker());
		
		startClientHttp(ClientGlobal.port);
		setClientReady(true);
	}
}
