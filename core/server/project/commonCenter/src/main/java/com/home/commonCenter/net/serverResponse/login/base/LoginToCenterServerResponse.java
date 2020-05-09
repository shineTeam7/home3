package com.home.commonCenter.net.serverResponse.login.base;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class LoginToCenterServerResponse extends CenterServerResponse
{
	/** 游戏服ID */
	protected int fromLoginID;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromLoginID=CenterC.server.getInfoIDBySocketID(SocketType.Login,socket.id);
	}
}
