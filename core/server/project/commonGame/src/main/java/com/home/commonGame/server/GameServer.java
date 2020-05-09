package com.home.commonGame.server;

import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonBase.data.system.GameServerInfoData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.server.BaseGameServer;
import com.home.commonGame.global.GameC;
import com.home.commonGame.net.base.GameServerRequest;
import com.home.commonGame.net.response.system.SendClientLogResponse;
import com.home.commonGame.net.serverRequest.center.system.BeGameToCenterServerRequest;
import com.home.commonGame.net.serverRequest.game.system.BeGameToGameServerRequest;
import com.home.commonGame.net.serverRequest.manager.BeGameToManagerServerRequest;
import com.home.commonGame.tool.generate.GameRequestMaker;
import com.home.commonGame.tool.generate.GameResponseBindTool;
import com.home.commonGame.tool.generate.GameResponseMaker;
import com.home.commonGame.tool.generate.GameServerRequestMaker;
import com.home.commonGame.tool.generate.GameServerResponseMaker;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.support.collection.IntObjectMap;

/** 逻辑服server */
public class GameServer extends BaseGameServer
{
	/** 信息数据 */
	private GameServerInfoData _info;
	
	/** 全部游戏服简版信息 */
	private IntObjectMap<GameServerSimpleInfoData> _gameSimpleInfoDic;
	/** 逻辑服列表 */
	private int[] _gameList;
	/** 登陆服列表 */
	private int[] _loginList;
	
	private ServerSimpleInfoData _centerInfo;
	
	public GameServer()
	{
	
	}
	
	@Override
	protected ReceiveSocket toCreateClientReceiveSocket()
	{
		return new GameReceiveSocket();
	}
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		//忽略log
		BytesControl.addIgnoreMessage(SendClientLogResponse.dataID);
		
		addRequestMaker(new GameRequestMaker());
		addClientResponseMaker(new GameResponseMaker());
		addClientResponseBind(new GameResponseBindTool());
		
		addRequestMaker(new GameServerRequestMaker());
		addServerResponseMaker(new GameServerResponseMaker());
		
	}
	
	@Override
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
		socket.send(BeGameToManagerServerRequest.create(GameC.app.id,isFirst));
	}
	
	@Override
	protected void onConnectManagerOver()
	{
		GameC.app.startNext();
		
		connectServer(_centerInfo,SocketType.Center,true);
	}
	
	/** 初始化后续 */
	public void initNext()
	{
		_gameSimpleInfoDic.forEachValue(v->
		{
			//大的主动连小的
			if(v.id<GameC.app.id)
			{
				connectServer(v,SocketType.Game,false);
			}
		});
		
		startServerSocket(_info.serverPort);
		
		checkNext();
	}
	
	public void checkNext()
	{
		if(GameC.app.isInitLast())
			return;
		
		//if(isServerSendSocketAllReady() && isGameSocketAllReady())
		if(isGameSocketAllReady())
		{
			GameC.app.initLast();
		}
	}
	
	/** 逻辑服是否全连接好 */
	public boolean isGameSocketAllReady()
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
	
	/** 开启客户端端口 */
	public void openClient()
	{
		startClientSocket(_info.clientPort,CommonSetting.clientSocketUseWebSocket);
		setClientReady(true);
	}
	
	@Override
	protected void onSendConnectSuccessOnMain(SendSocket socket,boolean isFirst)
	{
		super.onSendConnectSuccessOnMain(socket,isFirst);
		
		if(socket.type==SocketType.Center)
		{
			socket.send(BeGameToCenterServerRequest.create(GameC.app.id,GameC.app.isInitLast()));
		}
		else if(socket.type==SocketType.Game)
		{
			BeGameToGameServerRequest.create(GameC.app.id,GameC.global.createLoginToGameData()).sendTo(socket);
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
}
