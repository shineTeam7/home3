package com.home.commonLogin.net.serverResponse.login.base;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class LoginToLoginServerResponse extends LoginServerResponse
{
	/** 来源登陆服ID */
	protected int fromLoginID;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromLoginID=LoginC.server.getInfoIDBySocketID(SocketType.Login,socket.id);
	}
}
