package com.home.commonGame.server;

import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonBase.data.system.GameServerInfoData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonGame.constlist.generate.GameRequestType;
import com.home.commonGame.constlist.generate.GameResponseType;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;
import com.home.commonGame.net.response.system.SendClientLogResponse;
import com.home.commonGame.net.serverRequest.center.system.BeGameToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.system.BeGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.manager.BeGameToManagerServerRequest;
import com.home.commonGame.net.serverRequest.scene.system.BeGameToSceneServerRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonGame.tool.generate.GameRequestMaker;
import com.home.commonGame.tool.generate.GameResponseBindTool;
import com.home.commonGame.tool.generate.GameResponseMaker;
import com.home.commonGame.tool.generate.GameServerRequestMaker;
import com.home.commonGame.tool.generate.GameServerResponseMaker;
import com.home.commonSceneBase.net.base.SceneBaseResponse;
import com.home.commonSceneBase.server.SceneBaseServer;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;

/** 逻辑服server */
public class GameServer extends SceneBaseServer
{
	/** 信息数据 */
	private GameServerInfoData _info;
	
	/** 全部游戏服简版信息 */
	private IntObjectMap<GameServerSimpleInfoData> _gameSimpleInfoDic;
	/** 全部场景服简版信息 */
	private IntObjectMap<ServerSimpleInfoData> _sceneSimpleInfoDic;
	
	/** 逻辑服列表 */
	private int[] _gameList;
	/** 登陆服列表 */
	private int[] _loginList;
	
	private ServerSimpleInfoData _centerInfo;
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		
		BytesControl.addMessageConst(GameRequestType.class,true,false);
		BytesControl.addMessageConst(GameResponseType.class,false,false);
		
		addRequestMaker(new GameRequestMaker());
		addClientResponseMaker(new GameResponseMaker());
		addClientResponseBind(new GameResponseBindTool());
		
		addRequestMaker(new GameServerRequestMaker());
		addServerResponseMaker(new GameServerResponseMaker());
		
		//忽略log
		BytesControl.addIgnoreMessage(SendClientLogResponse.dataID);
	}
	
	@Override
	protected ReceiveSocket toCreateClientReceiveSocket()
	{
		return new GameReceiveSocket();
	}
	
	@Override
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
		socket.send(BeGameToManagerServerRequest.create(GameC.app.id,isFirst));
	}
	
	@Override
	public void onConnectManagerOver()
	{
		GameC.app.startNext();
		
		connectServer(_centerInfo,SocketType.Center,true);
	}
	
	/** 初始化后续 */
	public void initNext()
	{
		connectOthers();
		
		startServerSocket(_info.serverPort);
		
		checkNext();
	}
	
	public void connectOthers()
	{
		_gameSimpleInfoDic.forEachValue(v->
		{
			//大的主动连小的
			if(v.id<GameC.app.id)
			{
				connectServer(v,SocketType.Game,false);
			}
		});
		
		if(CommonSetting.useSceneServer)
		{
			_sceneSimpleInfoDic.forEachValue(v->
			{
				connectServer(v,SocketType.Scene,false);
			});
		}
	}
	
	public void checkNext()
	{
		if(GameC.app.isInitLast())
			return;
		
		if(isGameSocketAllReady() && isSceneSocketReady())
		{
			GameC.app.initLast();
		}
	}
	
	/** 逻辑服是否全连接好 */
	protected boolean isGameSocketAllReady()
	{
		SocketInfoDic socketInfo=getSocketInfo(SocketType.Game);
		
		BaseSocket socket;
		
		GameServerSimpleInfoData[] values;
		GameServerSimpleInfoData v;
		
		for(int i=(values=_gameSimpleInfoDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				//必须且不是自己
				if(v.isNecessary && v.id!=GameC.app.id)
				{
					if(!((socket=socketInfo.getSocket(v.id))!=null && socket.isConnect()))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/** 是否有一个连接好的Scene */
	protected boolean isSceneSocketReady()
	{
		//不启用
		if(!CommonSetting.useSceneServer)
			return true;
		
		if(CommonSetting.isAreaSplit())
		{
			int sceneServerID=GameC.main.getSelfSceneServerID();
			
			if(sceneServerID==-1)
			{
				Ctrl.errorLog("找不到自身所属场景服");
				return true;
			}
			
			BaseSocket socket=getServerSocket(SocketType.Scene,sceneServerID);
			
			return socket!=null && socket.isConnect();
		}
		else
		{
			SocketInfoDic socketInfo=getSocketInfo(SocketType.Scene);
			
			BaseSocket socket;
			
			ServerSimpleInfoData[] values;
			ServerSimpleInfoData v;
			
			for(int i=(values=_sceneSimpleInfoDic.getValues()).length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					//有一条连接就算
					if(!((socket=socketInfo.getSocket(v.id))!=null && socket.isConnect()))
					{
						return true;
					}
				}
			}
			
			return false;
		}
	}
	
	@Override
	protected void onServerSocketClosedOnMain(BaseSocket socket)
	{
		//场景服
		if(socket.type==SocketType.Scene)
		{
			//移除
			GameC.main.setSceneServerPlayerNum(((SendSocket)socket).sendID,-1);
		}
	}
	
	@Override
	protected void onSendConnectSuccessOnMain(SendSocket socket,boolean isFirst)
	{
		super.onSendConnectSuccessOnMain(socket,isFirst);
		
		switch(socket.type)
		{
			case SocketType.Center:
			{
				socket.send(BeGameToCenterServerRequest.create(GameC.app.id,GameC.app.isInitLast()));
			}
				break;
			case SocketType.Game:
			{
				socket.send(BeGameToGameServerRequest.create(GameC.app.id,GameC.global.createLoginToGameData()));
			}
				break;
			case SocketType.Scene:
			{
				socket.send(BeGameToSceneServerRequest.create(GameC.app.id));
			}
				break;
		}
	}
	
	@Override
	protected void onNewClientSocket(BaseSocket socket)
	{
		super.onNewClientSocket(socket);

		Ctrl.debugLog("收到新客户端连接",socket.id,"ip:",socket.remoteIP(),"port:",socket.remotePort());
	}
	
	@Override
	protected void onClientSocketClosed(BaseSocket socket)
	{
		super.onClientSocketClosed(socket);
		
		//切到主线程
		ThreadControl.addMainFunc(()->
		{
			GameC.main.clientSocketClosed(socket);
		});
	}
	
	
	public void setInfos(GameInitServerData initData)
	{
		_centerInfo=initData.centerInfo;
		_gameSimpleInfoDic=initData.gameServerDic;
		_sceneSimpleInfoDic=initData.sceneServerDic;
		_gameList=_gameSimpleInfoDic.getSortedKeyList().toArray();
		_loginList=initData.loginList;
		GameC.db.setURL(initData.info.mysql);
		GameC.db.setCenterURL(initData.centerInfo.mysql);
		setSelfInfo(_info=initData.info);
	}
	
	public GameServerInfoData getInfo()
	{
		return _info;
	}
	
	/** 获取游戏服简版信息数据 */
	public IntObjectMap<GameServerSimpleInfoData> getGameSimpleInfoDic()
	{
		return _gameSimpleInfoDic;
	}
	
	/** 获取逻辑服列表 */
	public int[] getGameList()
	{
		return _gameList;
	}
	
	/** 获取登陆服列表 */
	public int[] getLoginList()
	{
		return _loginList;
	}
	
	/** 广播所有登录服 */
	public void radioLogins(GameServerRequest request)
	{
		request.write();
		
		BaseSocket[] values;
		BaseSocket v;
		
		for(int i=(values=getSocketInfo(SocketType.Login).socketDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.send(request);
			}
		}
	}
	
	/** 广播所有逻辑服(不包括自己) */
	public void radioGames(GameServerRequest request)
	{
		request.write();
		
		BaseSocket[] values;
		BaseSocket v;
		
		for(int i=(values=getSocketInfo(SocketType.Game).socketDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.send(request);
			}
		}
	}
	
	@Override
	protected void dispatchClientResponse(BaseSocket socket,BaseResponse response)
	{
		//场景消息
		if(response instanceof SceneBaseResponse)
		{
			SceneBaseResponse sceneR=(SceneBaseResponse)response;
			
			Player player;
			
			if((player=((GameReceiveSocket)socket).player)!=null)
			{
				GameScene scene=player.scene.getScene();
				
				if(scene!=null)
				{
					sceneR.setInfo(scene,player.role.playerID);
					//直接加到执行器
					scene.getExecutor().addFunc(sceneR);
				}
			}
		}
		//else if(response instanceof GameResponse)
		//{
		//	GameResponse gameR=(GameResponse)response;
		//
		//	Player player;
		//
		//	if((player=((GameReceiveSocket)socket).player)==null)
		//	{
		//		gameR.doPlayerNull();
		//	}
		//	else
		//	{
		//		gameR.setPlayer(player);
		//
		//		if(gameR.getThreadType()==ThreadType.Main)
		//		{
		//			//主线程
		//			player.addMainFunc(gameR);
		//		}
		//		else
		//		{
		//			//派发
		//			player.addFunc(response);
		//		}
		//	}
		//}
		else
		{
			response.dispatch();
		}
	}
}
