package com.home.commonLogin.server;

import com.home.commonBase.data.login.LoginInitServerData;
import com.home.commonBase.data.system.GameServerSimpleInfoData;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.server.BaseGameServer;
import com.home.commonLogin.global.LoginC;
import com.home.commonLogin.net.serverRequest.game.system.BeLoginToGameServerRequest;
import com.home.commonLogin.net.serverRequest.login.system.BeLoginToLoginServerRequest;
import com.home.commonLogin.net.serverRequest.manager.system.BeLoginToManagerServerRequest;
import com.home.commonLogin.tool.generate.LoginHttpResponseMaker;
import com.home.commonLogin.tool.generate.LoginServerRequestMaker;
import com.home.commonLogin.tool.generate.LoginServerResponseMaker;
import com.home.shine.constlist.SocketType;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.http.ServerHttp;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.support.collection.IntObjectMap;

public class LoginServer extends BaseGameServer
{
	/** 全部登陆服简版信息 */
	private IntObjectMap<ServerSimpleInfoData> _loginSimpleInfoDic;
	/** 全部游戏服简版信息 */
	protected IntObjectMap<GameServerSimpleInfoData> _gameSimpleInfoDic;
	
	private int[] _loginList;
	
	public LoginServer()
	{
	
	}
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		addRequestMaker(new LoginServerRequestMaker());
		addServerResponseMaker(new LoginServerResponseMaker());
		addClientBytesHttpResponseMaker(new LoginHttpResponseMaker());
		
	}
	
	@Override
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
		socket.send(BeLoginToManagerServerRequest.create(LoginC.app.id,isFirst));
	}
	
	@Override
	public void onConnectManagerOver()
	{
		LoginC.app.startNext();
		
		LoginC.app.initNext();
	}
	
	/** 初始化后续 */
	public void initNext()
	{
		connectOthers();
		
		startServerSocket(_selfInfo.serverPort);
		
		checkNext();
	}
	
	/** 连接其他服 */
	public void connectOthers()
	{
		_loginSimpleInfoDic.forEachValue(v->
		{
			//大的主动连小的
			if(v.id<LoginC.app.id)
			{
				connectServer(v,SocketType.Login,false);
			}
		});
		
		_gameSimpleInfoDic.forEachValue(v->
		{
			connectServer(v,SocketType.Game,false);
		});
	}
	
	public void checkNext()
	{
		if(LoginC.app.isInitLast())
			return;
		
		//if(isServerSendSocketAllReady())
		if(isGameSocketAllReady())
		{
			LoginC.app.initLast();
		}
	}
	
	/** 逻辑服是否全连接好 */
	public boolean isGameSocketAllReady()
	{
		SocketInfoDic socketInfo=getSocketInfo(SocketType.Game);
		SocketSendInfoDic socketSendInfo=getSocketSendInfo(SocketType.Game);
		
		BaseSocket socket;
		
		GameServerSimpleInfoData[] values;
		GameServerSimpleInfoData v;
		
		for(int i=(values=_gameSimpleInfoDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				if(v.isNecessary)
				{
					//还在等
					if(socketSendInfo.waitReSet.contains(v.id))
					{
						return false;
					}
					
					if(!((socket=socketInfo.getSocket(v.id))!=null && socket.isConnect()))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public void openClient()
	{
		ServerHttp serverHttp=startClientHttp(_selfInfo.clientHttpPort);
		
		if(ShineSetting.clientHttpUseBase64)
		{
			serverHttp.setNeedBase64(true);
		}
		
		setClientReady(true);
	}
	
	@Override
	protected void onSendConnectSuccessOnMain(SendSocket socket,boolean isFirst)
	{
		super.onSendConnectSuccessOnMain(socket,isFirst);
		
		switch(socket.type)
		{
			case SocketType.Game:
			{
				socket.send(BeLoginToGameServerRequest.create(LoginC.app.id));
			}
				break;
			case SocketType.Login:
			{
				socket.send(BeLoginToLoginServerRequest.create(LoginC.app.id));
			}
				break;
		}
	}
	
	/** 设置信息组 */
	public void setInfos(LoginInitServerData initData)
	{
		_loginSimpleInfoDic=initData.loginServerDic;
		_gameSimpleInfoDic=initData.gameServerDic;
		LoginC.db.setURL(initData.info.mysql);
		
		int[] loginList=new int[_loginSimpleInfoDic.size()];
		
		int i=0;
		
		for(ServerSimpleInfoData v : _loginSimpleInfoDic)
		{
			loginList[i++]=v.id;
		}
		
		_loginList=loginList;
		
		setSelfInfo(initData.info);
	}
	
	public GameServerSimpleInfoData getGameSimpleInfo(int gameID)
	{
		return _gameSimpleInfoDic.get(gameID);
	}
	
	public int[] getLoginList()
	{
		return _loginList;
	}
}
