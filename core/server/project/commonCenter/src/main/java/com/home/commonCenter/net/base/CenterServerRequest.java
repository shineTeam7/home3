package com.home.commonCenter.net.base;

import com.home.commonCenter.global.CenterC;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;

public class CenterServerRequest extends BaseRequest
{
	public void sendToGame(int gameID)
	{
		BaseSocket socket;
		if((socket=CenterC.server.getGameSocket(gameID))!=null)
		{
			socket.send(this);
		}
	}
}
