package com.home.commonGame.net.serverResponse.scene.base;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.SendSocket;

public abstract class SceneToGameServerResponse extends BaseResponse
{
	protected int fromSceneServerID;
	
	@Override
	public void dispatch()
	{
		fromSceneServerID=((SendSocket)socket).sendID;
		
		super.dispatch();
	}
}
