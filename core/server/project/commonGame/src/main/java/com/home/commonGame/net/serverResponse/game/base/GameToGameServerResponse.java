package com.home.commonGame.net.serverResponse.game.base;

import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerResponse;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.socket.BaseSocket;

public abstract class GameToGameServerResponse extends GameServerResponse
{
	/** 来源游戏服ID */
	protected int fromGameID=-1;
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromGameID=GameC.server.getInfoIDBySocketID(SocketType.Game,socket.id);
	}
}
