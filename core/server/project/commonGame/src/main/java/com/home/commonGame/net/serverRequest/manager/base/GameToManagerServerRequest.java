package com.home.commonGame.net.serverRequest.manager.base;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;
import com.home.shine.net.socket.BaseSocket;

public class GameToManagerServerRequest extends GameServerRequest
{
	/** 发送到manager服 */
	public void send()
	{
		BaseSocket managerSocket=GameC.server.getManagerSocket();
		if(managerSocket!=null){
			sendTo(managerSocket);
		}
	}
}
