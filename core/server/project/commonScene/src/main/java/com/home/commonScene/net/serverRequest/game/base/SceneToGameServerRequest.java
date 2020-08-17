package com.home.commonScene.net.serverRequest.game.base;
import com.home.commonScene.global.SceneC;
import com.home.shine.constlist.SocketType;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;

public class SceneToGameServerRequest extends BaseRequest
{
	/** 发送给某场景服 */
	public void send(int gameID)
	{
		BaseSocket socket;
		
		if((socket=SceneC.server.getServerSocket(SocketType.Game,gameID))!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.errorLog("gameSocket丢失",getDataID(),gameID);
		}
	}
}
