package com.home.commonGame.net.serverRequest.scene.base;
import com.home.commonGame.global.GameC;
import com.home.shine.constlist.SocketType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;

public class GameToSceneServerRequest extends BaseRequest
{
	/** 发送给某场景服 */
	public void send(int sceneServerID)
	{
		BaseSocket socket;
		
		if((socket=GameC.server.getServerSocket(SocketType.Scene,sceneServerID))!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.errorLog("sceneSocket丢失",getDataID(),sceneServerID);
		}
	}
}
