package com.home.commonCenter.net.serverRequest.login.base;

import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerRequest;
import com.home.shine.constlist.SocketType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

public class CenterToLoginServerRequest extends CenterServerRequest
{
	public void send(int loginID)
	{
		BaseSocket socket;
		
		if((socket=CenterC.server.getServerSocket(SocketType.Login,loginID))!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.warnLog("loginSocket丢失",getDataID(),loginID);
		}
	}
}
