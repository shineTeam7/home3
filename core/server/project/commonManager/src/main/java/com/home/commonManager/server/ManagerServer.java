package com.home.commonManager.server;

import com.home.commonBase.server.BaseGameServer;
import com.home.commonManager.tool.ManagerClientHttpResponseMaker;
import com.home.commonManager.tool.generate.ManagerHttpResponseMaker;
import com.home.commonManager.tool.generate.ManagerServerRequestMaker;
import com.home.commonManager.tool.generate.ManagerServerResponseMaker;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.serverConfig.BaseServerConfig;
import com.home.shine.serverConfig.ServerConfig;

public class ManagerServer extends BaseGameServer
{
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		addRequestMaker(new ManagerServerRequestMaker());
		addServerResponseMaker(new ManagerServerResponseMaker());
		addClientBytesHttpResponseMaker(new ManagerHttpResponseMaker());
		
		//http接收器
		initHttpResponseMaker();
	}
	
	/** http接收器 为方便复写 */
	protected void initHttpResponseMaker()
	{
		setClientHttpResponseMaker(new ManagerClientHttpResponseMaker());
	}
	
	@Override
	public void init()
	{
		super.init();
		
		setClientReady(true);
		setServerReady(true);
		
		BaseServerConfig managerConfig=ServerConfig.getManagerConfig();
		
		startClientHttp(managerConfig.clientHttpPort);
		startServerSocket(managerConfig.serverPort);
	}
	
	@Override
	protected void onNewClientSocket(BaseSocket socket)
	{
		super.onNewClientSocket(socket);
	}
	
	@Override
	protected void onServerSocketClosed(BaseSocket socket)
	{
		super.onServerSocketClosed(socket);
		
		////主服
		//if(socket==_centerSocket && !ShineSetup.isExiting())
		//{
		//	Ctrl.debugLog("中心服连接中断,尝试重连");
		//	_centerSocket.reTryConnect();
		//}
	}
	
	/** 通知下属进程退出 */
	public void noticeExit()
	{
	
	}
}
