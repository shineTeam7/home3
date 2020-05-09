package com.home.commonCenter.net.serverRequest.game.base;

import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerRequest;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;

/** 中心服到逻辑服消息 */
public class CenterToGameServerRequest extends CenterServerRequest
{
	public void send(int gameID)
	{
		BaseSocket socket;
		
		if((socket=CenterC.server.getGameSocket(gameID))!=null)
		{
			socket.send(this);
		}
		else
		{
			Ctrl.errorLog("gameSocket丢失",getDataID(),gameID);
		}
	}
	
	/** 通过playerID,发送回所在源服 */
	public void sendToSourceGameByPlayerID(long playerID)
	{
		send(CenterC.main.getNowGameIDByLogicID(playerID));
	}
}
