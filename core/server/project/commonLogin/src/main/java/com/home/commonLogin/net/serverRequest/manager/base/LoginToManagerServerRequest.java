package com.home.commonLogin.net.serverRequest.manager.base;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerRequest;
import com.home.shine.net.socket.BaseSocket;

public class LoginToManagerServerRequest extends LoginServerRequest
{
	/** 发送到manager服 */
	public void send()
	{
		BaseSocket managerSocket=LoginC.server.getManagerSocket();
		if(managerSocket!=null){
			sendTo(managerSocket);
		}
	}
}
