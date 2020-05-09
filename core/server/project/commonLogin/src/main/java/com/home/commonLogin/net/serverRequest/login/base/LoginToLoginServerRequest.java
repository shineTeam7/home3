package com.home.commonLogin.net.serverRequest.login.base;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.base.LoginServerRequest;
import com.home.shine.constlist.SocketType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

public class LoginToLoginServerRequest extends LoginServerRequest
{
	/** 发送到某登陆服 */
	public void send(int loginID)
	{
		BaseSocket socket=LoginC.server.getSocketInfo(SocketType.Login).getSocket(loginID);
		
		if(socket!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.warnLog("登陆服连接丢失:",loginID);
		}
	}
}
