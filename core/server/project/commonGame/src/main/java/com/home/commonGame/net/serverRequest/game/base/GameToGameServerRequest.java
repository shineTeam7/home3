package com.home.commonGame.net.serverRequest.game.base;

import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.support.pool.BytesWriteStreamPool;
import com.home.shine.thread.AbstractThread;

public class GameToGameServerRequest extends GameServerRequest
{
	/** 发送到指定游戏服 */
	public void send(int gameID)
	{
		if(gameID==GameC.app.id)
		{
			//Ctrl.warnLog("出现一次发送GameToGame消息给自己",_dataID,gameID);
			
			preSend();
			ThreadControl.addIOFunc(this.hashCode() & ThreadControl.ioThreadNumMark,this::sendSelfOnIO);
		}
		else
		{
			BaseSocket socket;
			
			if((socket=GameC.server.getServerSocket(SocketType.Game,gameID))!=null)
			{
				socket.send(this);
			}
			else
			{
				Ctrl.errorLog("gameSocket丢失",getDataID(),gameID);
			}
		}
	}
	
	/** 通过playerID / groupID,发送回所在源服 */
	public void sendToSourceGameByLogicID(long logicID)
	{
		send(GameC.main.getNowGameIDByLogicID(logicID));
	}
	
	protected void sendSelfOnIO()
	{
		BytesWriteStream stream=BytesWriteStreamPool.create();
		doWriteToStream(stream);
		
		BaseResponse response=GameC.server.createServerResponse(_dataID);
		BytesReadStream rStream=BytesReadStream.create(stream.getByteArray());
		BytesWriteStreamPool.release(stream);
		
		response.readFromStream(rStream,GameC.server);
		response.dispatch();
	}
}
