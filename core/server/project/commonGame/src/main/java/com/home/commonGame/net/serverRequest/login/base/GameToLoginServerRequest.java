package com.home.commonGame.net.serverRequest.login.base;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;
import com.home.shine.constlist.SocketType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

public class GameToLoginServerRequest extends GameServerRequest
{
	public void send(int loginID)
	{
		BaseSocket socket=GameC.server.getServerSocket(SocketType.Login,loginID);
		
		if(socket!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.warnLog("登陆服连接丢失",loginID);
		}
	}
	
	/** 通过uid发送到对应login服 */
	public void sendByUID(String uid)
	{
		int[] loginList=GameC.server.getLoginList();
		
		int loginID=loginList[uid.hashCode() % loginList.length];
		
		send(loginID);
	}
}
