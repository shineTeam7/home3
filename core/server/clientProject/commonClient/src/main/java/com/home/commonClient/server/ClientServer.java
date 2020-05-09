package com.home.commonClient.server;

import com.home.commonBase.global.CommonSetting;
import com.home.commonClient.global.ClientC;
import com.home.commonClient.net.base.GameResponse;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.tool.generate.CenterRequestBindTool;
import com.home.commonClient.tool.generate.CenterRequestMaker;
import com.home.commonClient.tool.generate.CenterResponseMaker;
import com.home.commonClient.tool.generate.GameRequestBindTool;
import com.home.commonClient.tool.generate.GameRequestMaker;
import com.home.commonClient.tool.generate.GameResponseMaker;
import com.home.shine.constlist.SocketType;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.server.BaseServer;

/** 客户端连接 */
public class ClientServer extends BaseServer
{
	/** 角色 */
	public Player me;
	
	/** 连接 */
	private SendSocket _socket;
	
	public ClientServer(Player player)
	{
		me=player;
	}
	
	@Override
	public void init()
	{
		//只需要注册
		initMessage();
		
		//开关
		setClientReady(true);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		if(_socket!=null)
		{
			_socket.close();
			_socket=null;
		}
	}
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		//设置客户端构造器
		setClientResponseMaker(ClientC.main.getClientResponseMaker());

		addRequestMaker(new GameRequestMaker());
		addClientResponseMaker(new GameResponseMaker());
		addClientRequestBind(new GameRequestBindTool());
		addRequestMaker(new CenterRequestMaker());
		addClientResponseMaker(new CenterResponseMaker());
		addClientRequestBind(new CenterRequestBindTool());
	}
	
	@Override
	public BaseResponse createClientResponse(int mid)
	{
		BaseResponse response=super.createClientResponse(mid);
		
		if(response!=null && response instanceof GameResponse)
		{
			((GameResponse)response).setPlayer(me);
		}
		
		return response;
	}
	
	public void connectGame(String host,int port)
	{
		//if(_socket!=null)
		//{
		//	_socket.close();
		//	_socket=null;
		//}
		
		if(_socket==null)
		{
			_socket=createSendSocket(0,SocketType.ClientSend,true,CommonSetting.clientSocketUseWebSocket);
			//设置为可扩容
			_socket.setNeedBufGrow(true);
		}
		
		if(_socket.isConnect())
		{
			_socket.close();
		}
		
		_socket.connect(host,port);
	}
	
	@Override
	protected void onSendConnectSuccess(SendSocket socket)
	{
		me.addFunc(()->
		{
			me.system.connectGameSuccess();
		});
	}
	
	@Override
	protected void onSendConnectFailed(SendSocket socket)
	{
		me.addFunc(()->
		{
			me.system.connectGameFailed();
		});
		
	}
	
	@Override
	protected void onClientSocketClosed(BaseSocket socket)
	{
		super.onClientSocketClosed(socket);
		
		//不是主动关闭的
		if(!socket.lastCloseByInitiative())
		{
			me.addFunc(()->
			{
				me.system.onSocketClose();
			});
		}
	}
	
	/** 获取socket */
	public SendSocket getSocket()
	{
		return _socket;
	}
	
	public void close()
	{
		_socket.close();
	}
}
