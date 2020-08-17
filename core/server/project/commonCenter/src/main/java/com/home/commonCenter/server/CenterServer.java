package com.home.commonCenter.server;

import com.home.commonBase.server.BaseGameServer;
import com.home.commonCenter.constlist.generate.CenterRequestType;
import com.home.commonCenter.constlist.generate.CenterResponseType;
import com.home.commonCenter.global.CenterC;
import com.home.commonCenter.net.base.CenterServerRequest;
import com.home.commonCenter.net.serverRequest.game.system.GameExitServerRequest;
import com.home.commonCenter.net.serverRequest.manager.BeCenterToManagerServerRequest;
import com.home.commonCenter.tool.CenterServerHttpResponseMaker;
import com.home.commonCenter.tool.generate.CenterRequestMaker;
import com.home.commonCenter.tool.generate.CenterResponseBindTool;
import com.home.commonCenter.tool.generate.CenterResponseMaker;
import com.home.commonCenter.tool.generate.CenterServerRequestMaker;
import com.home.commonCenter.tool.generate.CenterServerResponseMaker;
import com.home.shine.ShineSetup;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.BytesControl;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.SendSocket;

/** commonServer */
public class CenterServer extends BaseGameServer
{
	public CenterServer()
	{
	
	}
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		BytesControl.addMessageConst(CenterRequestType.class,true,false);
		BytesControl.addMessageConst(CenterResponseType.class,false,false);
		
		addRequestMaker(new CenterRequestMaker());
		addClientResponseMaker(new CenterResponseMaker());
		addClientResponseBind(new CenterResponseBindTool());
		
		addRequestMaker(new CenterServerRequestMaker());
		addServerResponseMaker(new CenterServerResponseMaker());
		
		//http接收器
		initServerHttpResponseMaker();
	}
	
	/** http接收器 为方便复写 */
	protected void initServerHttpResponseMaker()
	{
		setServerHttpResponseMaker(new CenterServerHttpResponseMaker());
	}
	
	@Override
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
		socket.send(BeCenterToManagerServerRequest.create(isFirst));
	}
	
	@Override
	public void onConnectManagerOver()
	{
		startServerSocket(_selfInfo.serverPort);
		startServerHttp(_selfInfo.serverHttpPort);
		
		CenterC.app.startNext();
	}
	
	@Override
	protected void onServerSocketClosedOnMain(BaseSocket socket)
	{
		//没有连接了,并且退出中
		if(ShineSetup.isExiting() && isServerReceiveSocketEmpty())
		{
			CenterC.app.checkExitNext();
		}
	}
	
	/** 发到登录服 */
	public void sendToLogin(int loginID,CenterServerRequest request)
	{
		BaseSocket socket;
		
		if((socket=getServerSocket(SocketType.Login,loginID))!=null)
		{
			socket.send(request);
		}
	}
	
	/** 发到游戏服 */
	public void sendToGame(int gameID,CenterServerRequest request)
	{
		BaseSocket socket;
		
		if((socket=getServerSocket(SocketType.Game,gameID))!=null)
		{
			socket.send(request);
		}
	}
	
	/** 发送到manager */
	public void sendToManager(CenterServerRequest request)
	{
		BaseSocket socket=getSocketInfo(SocketType.Manager).onlySocket;
		
		if(socket!=null)
			socket.send(request);
	}
	
	/** 通知下属进程退出 */
	public void noticeExit()
	{
		radioGames(GameExitServerRequest.create());
	}
}
