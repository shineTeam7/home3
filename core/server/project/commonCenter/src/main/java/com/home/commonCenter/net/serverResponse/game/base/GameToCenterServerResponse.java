package com.home.commonCenter.net.serverResponse.game.base;

import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class GameToCenterServerResponse extends CenterServerResponse
{
	/** 游戏服ID */
	protected int fromGameID;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromGameID=CenterC.server.getInfoIDBySocketID(SocketType.Game,socket.id);
	}
}
