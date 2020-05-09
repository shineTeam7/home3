package com.home.commonGame.net.serverResponse.login.base;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class LoginToGameServerResponse extends GameServerResponse
{
	protected int fromLoginID;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromLoginID=GameC.server.getInfoIDBySocketID(SocketType.Login,socket.id);
	}
}
