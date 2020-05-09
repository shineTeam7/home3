package com.home.commonLogin.net.serverResponse.game.base;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class GameToLoginServerResponse extends LoginServerResponse
{
	protected int fromGameID;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromGameID=LoginC.server.getInfoIDBySocketID(SocketType.Game,socket.id);
	}
}
