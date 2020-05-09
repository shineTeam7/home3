package com.home.shine.server;

import com.home.shine.tool.IHttpResponseMaker;

public class BaseQueryServer extends BaseServer
{
	public void initQuery(int port,IHttpResponseMaker maker)
	{
		init();
		setServerHttpResponseMaker(maker);
		setServerReady(true);
		startServerHttp(port);
	}
}
