package com.home.commonScene.server;

import com.home.commonScene.constlist.generate.SceneRequestType;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.net.serverRequest.manager.BeSceneToManagerServerRequest;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.scene.base.SScene;
import com.home.commonScene.tool.generate.SceneRequestMaker;
import com.home.commonScene.tool.generate.SceneResponseBindTool;
import com.home.commonScene.tool.generate.SceneResponseMaker;
import com.home.commonScene.tool.generate.SceneServerRequestMaker;
import com.home.commonScene.tool.generate.SceneServerResponseMaker;
import com.home.commonSceneBase.net.base.SceneBaseResponse;
import com.home.commonSceneBase.server.SceneBaseServer;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.SendSocket;

public class SceneServer extends SceneBaseServer
{
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		BytesControl.addMessageConst(SceneRequestType.class,true,false);
		BytesControl.addMessageConst(SceneRequestType.class,false,false);
		
		addRequestMaker(new SceneRequestMaker());
		
		addClientResponseMaker(new SceneResponseMaker());
		addClientResponseBind(new SceneResponseBindTool());
		
		addRequestMaker(new SceneServerRequestMaker());
		addServerResponseMaker(new SceneServerResponseMaker());
	}
	
	@Override
	protected ReceiveSocket toCreateClientReceiveSocket()
	{
		return new SceneReceiveSocket();
	}
	
	@Override
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
		socket.send(BeSceneToManagerServerRequest.create(SceneC.app.id,isFirst));
	}
	
	@Override
	public void onConnectManagerOver()
	{
		startServerSocket(_selfInfo.serverPort);
		
		SceneC.app.startNext();
		
		openClient();
	}
	
	@Override
	protected void dispatchClientResponse(BaseSocket socket,BaseResponse response)
	{
		//场景消息
		if(response instanceof SceneBaseResponse)
		{
			SceneBaseResponse sceneR=(SceneBaseResponse)response;
			
			ScenePlayer player;
			
			if((player=((SceneReceiveSocket)socket).player)!=null)
			{
				SScene scene=player.getScene();
				
				if(scene!=null)
				{
					sceneR.setInfo(scene,player.playerID);
					//直接加到执行器
					scene.getExecutor().addFunc(sceneR);
				}
			}
		}
		else
		{
			response.dispatch();
		}
	}
	
	@Override
	protected void onClientSocketClosed(BaseSocket socket)
	{
		super.onClientSocketClosed(socket);
		
		//切到主线程
		ThreadControl.addMainFunc(()->
		{
			SceneC.main.clientSocketClosed(socket);
		});
	}
}
