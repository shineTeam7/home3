package com.home.commonGMClient.net.base;

import com.home.shine.net.httpRequest.BytesHttpRequest;
import com.home.shine.serverConfig.BaseServerConfig;
import com.home.shine.serverConfig.ServerConfig;

public abstract class ManagerHttpRequest extends BytesHttpRequest
{
	@Override
	protected void doSendSync()
	{
		sendToManager(true);
	}
	
	/** 发送到manager服 */
	public void sendToManager()
	{
		sendToManager(false);
	}
	
	/** 发送到manager服 */
	public void sendToManager(boolean isSync)
	{
		BaseServerConfig managerConfig=ServerConfig.getManagerConfig();
		
		sendToHP(managerConfig.clientHost,managerConfig.clientHttpPort,isSync);
	}
}
