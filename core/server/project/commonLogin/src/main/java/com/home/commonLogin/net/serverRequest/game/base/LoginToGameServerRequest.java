package com.home.commonLogin.net.serverRequest.game.base;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

public class LoginToGameServerRequest extends LoginServerRequest
{
	public void send(int gameID)
	{
		BaseSocket socket;
		
		if((socket=LoginC.server.getGameSocket(gameID))!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.errorLog("gameSocket丢失",getDataID(),gameID);
		}
	}
	
	/** 通过playerID,发送回所在源服 */
	public void sendToSourceGameByPlayerID(long playerID)
	{
		send(LoginC.main.getNowGameIDByPlayerID(playerID));
	}
}
