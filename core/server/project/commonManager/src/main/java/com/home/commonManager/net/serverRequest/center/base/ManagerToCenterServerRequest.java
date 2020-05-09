package com.home.commonManager.net.serverRequest.center.base;
import com.home.commonManager.global.ManagerC;
import com.home.commonManager.net.base.ManagerServerRequest;

public class ManagerToCenterServerRequest extends ManagerServerRequest
{
	/** 发送到中心服 */
	public void send()
	{
		sendTo(ManagerC.server.getCenterSocket());
	}
}
