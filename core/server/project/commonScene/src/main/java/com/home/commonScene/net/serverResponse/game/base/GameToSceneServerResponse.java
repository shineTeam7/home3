package com.home.commonScene.net.serverResponse.game.base;
import com.home.commonScene.global.SceneC;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.BaseSocket;

public abstract class GameToSceneServerResponse extends BaseResponse
{
	/** 游戏服ID */
	protected int fromGameID;
	
	public GameToSceneServerResponse()
	{
		setUseMainThread();
	}
	
	@Override
	public void setSocket(BaseSocket socket)
	{
		super.setSocket(socket);
		
		fromGameID=SceneC.server.getInfoIDBySocketID(SocketType.Game,socket.id);
	}
}
