package com.home.commonBase.server;

import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.data.system.ServerInfoData;
import com.home.commonBase.data.system.ServerSimpleInfoData;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.serverConfig.ServerNodeConfig;
import com.home.shine.ShineSetup;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.server.BaseServer;
import com.home.shine.serverConfig.BaseServerConfig;
import com.home.shine.support.collection.IntIntMap;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.IntSet;

/** 服务器基础server */
public class BaseGameServer extends BaseServer
{
	protected final SocketInfoDic[] _infoDic=new SocketInfoDic[SocketType.size];
	
	protected final SocketSendInfoDic[] _sendInfoDic=new SocketSendInfoDic[SocketType.size];
	
	/** 自身信息数据 */
	protected ServerInfoData _selfInfo;
	
	@Override
	protected void initMessage()
	{
		super.initMessage();
		
		BytesControl.addMessageConst(ServerMessageType.class,true,true);
		BytesControl.addMessageConst(ServerMessageType.class,false,true);
	}
	
	public SocketInfoDic getSocketInfo(int type)
	{
		SocketInfoDic re;
		
		if((re=_infoDic[type])==null)
		{
			synchronized(_infoDic)
			{
				if((re=_infoDic[type])==null)
				{
					re=_infoDic[type]=new SocketInfoDic(type);
				}
			}
		}
		
		return re;
	}
	
	public SocketSendInfoDic getSocketSendInfo(int type)
	{
		SocketSendInfoDic re;
		
		if((re=_sendInfoDic[type])==null)
		{
			synchronized(_sendInfoDic)
			{
				if((re=_sendInfoDic[type])==null)
				{
					re=_sendInfoDic[type]=new SocketSendInfoDic(type);
				}
			}
		}
		
		return re;
	}
	
	public ServerInfoData getSelfInfo()
	{
		return _selfInfo;
	}
	
	/** 连接manager服 */
	public void connectManager()
	{
		setServerReady(true);
		
		connectServer(ServerNodeConfig.managerConfig,SocketType.Manager,true);
	}
	
	/** 推送获取信息到manager */
	protected void sendGetInfoToManager(SendSocket socket,boolean isFirst)
	{
	
	}
	
	/** 连接manager服部分结束 */
	public void setSelfInfo(ServerInfoData selfInfo)
	{
		_selfInfo=selfInfo;
	}
	
	/** 连接manager服部分结束 */
	public void onConnectManagerOver()
	{
	
	}
	
	/** 开启客户端端口 */
	public void openClient()
	{
		startClientSocket(_selfInfo.clientPort,CommonSetting.clientSocketUseWebSocket);
		setClientReady(true);
	}
	
	@Override
	protected void onSendConnectSuccess(SendSocket socket)
	{
		ThreadControl.addMainFunc(()->
		{
			getSocketInfo(socket.type).registerSocket(socket.sendID,socket);
			
			boolean lastHas=socket.hasConnectSuccessed;
			socket.hasConnectSuccessed=true;
			
			onSendConnectSuccessOnMain(socket,!lastHas);
		});
	}
	
	protected void onSendConnectSuccessOnMain(SendSocket socket,boolean isFirst)
	{
		if(socket.type==SocketType.Manager)
		{
			sendGetInfoToManager(socket,isFirst);
		}
	}
	
	@Override
	protected void onServerSocketClosed(BaseSocket socket)
	{
		super.onServerSocketClosed(socket);
		
		ThreadControl.addMainFunc(()->
		{
			//连接关闭
			getSocketInfo(socket.type).socketClose(socket.id);
			
			if(!ShineSetup.isExiting())
			{
				//发送连接
				if(socket instanceof SendSocket)
				{
					Ctrl.debugLog("服务器连接中断,尝试重连",socket.type,((SendSocket)socket).sendID);
					((SendSocket)socket).reTryConnect();
				}
			}
			
			onServerSocketClosedOnMain(socket);
		});
	}
	
	protected void onServerSocketClosedOnMain(BaseSocket socket)
	{
	
	}
	
	/** 接收连接是否全空了 */
	public boolean isServerReceiveSocketEmpty()
	{
		for(SocketInfoDic info : _infoDic)
		{
			if(info!=null && !info.socketDic.isEmpty())
				return false;
		}
		
		return true;
	}
	
	/** 是否全部收到回复(对于主动连接) */
	public boolean isServerSendSocketAllReady()
	{
		for(SocketSendInfoDic info : _sendInfoDic)
		{
			if(info!=null && !info.waitReSet.isEmpty())
				return false;
		}
		
		return true;
	}
	
	/** 连接服务器节点 */
	public void connectServer(BaseServerConfig config,int type,boolean isOnly)
	{
		int id=isOnly ? 1 : config.id;
		SendSocket socket=createSendSocket(id,type,false);
		getSocketSendInfo(type).add(id,socket);
		socket.tryConnect(config.serverHost,config.serverPort);
	}
	
	/** 连接服务器节点 */
	public void connectServer(ServerSimpleInfoData config,int type,boolean isOnly)
	{
		SocketSendInfoDic socketSendInfo=getSocketSendInfo(type);
		
		int id=isOnly ? 1 : config.id;
		
		//已存在
		if(socketSendInfo.has(id))
		{
			return;
		}
		
		SendSocket socket=createSendSocket(id,type,false);
		socketSendInfo.add(id,socket);
		socket.tryConnect(config.serverHost,config.serverPort);
	}
	
	/** 获取服务器连接 */
	public BaseSocket getServerSocket(int type,int id)
	{
		return getSocketInfo(type).getSocket(id);
	}
	
	/** 获取信息id */
	public int getInfoIDBySocketID(int type,int socketID)
	{
		return getSocketInfo(type).getInfoIDBySocketID(socketID);
	}
	
	/** 发送连接回复 */
	public void reBack(int type)
	{
		reBack(type,1);
	}
	
	/** 发送连接回复 */
	public void reBack(int type,int sendID)
	{
		getSocketSendInfo(type).waitReSet.remove(sendID);
	}
	
	/** 广播所有游戏服 */
	public void radioGames(BaseRequest request)
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
	
	/** 广播所有登录服 */
	public void radioLogins(BaseRequest request)
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
	
	/** 广播所有场景服 */
	public void radioScenes(BaseRequest request)
	{
		request.write();
		
		BaseSocket[] values;
		BaseSocket v;
		
		for(int i=(values=getSocketInfo(SocketType.Scene).socketDic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.send(request);
			}
		}
	}
	
	/** socket信息字典 */
	public static class SocketInfoDic
	{
		public int type;
		/** infoID->socket */
		public IntObjectMap<BaseSocket> socketDic;
		/** socketID->infoID */
		public IntIntMap infoDic;
		/** 唯一连接 */
		public BaseSocket onlySocket;
		
		public SocketInfoDic(int type)
		{
			this.type=type;
			
			socketDic=new IntObjectMap<>(BaseSocket[]::new);
			infoDic=new IntIntMap();
		}
		
		/** 注册连接 */
		public void registerSocket(int infoID,BaseSocket socket)
		{
			BaseSocket oldSocket=socketDic.get(infoID);
			
			if(oldSocket!=null)
			{
				Ctrl.warnLog("已存在服务器连接，可能是断线重连",this.type,infoID);
				infoDic.remove(oldSocket.id);
			}
			
			socketDic.put(infoID,socket);
			infoDic.put(socket.id,infoID);
			socket.setType(this.type);
			
			if(infoID==1)
			{
				onlySocket=socket;
			}
		}
		
		/** 注册唯一连接 */
		public void registerOnlySocket(BaseSocket socket)
		{
			registerSocket(1,socket);
		}
		
		/** 连接被关闭 */
		public void socketClose(int socketID)
		{
			if(infoDic.contains(socketID))
			{
				socketDic.remove(infoDic.remove(socketID));
			}
		}
		
		/** 获取连接 */
		public BaseSocket getSocket(int id)
		{
			return socketDic.get(id);
		}
		
		/** 通过连接id获取key */
		public int getInfoIDBySocketID(int socketID)
		{
			return infoDic.get(socketID);
		}
	}
	
	/** socket信息字典 */
	public static class SocketSendInfoDic
	{
		/** 发送字典 */
		public IntObjectMap<SendSocket> sendDic=new IntObjectMap<>(SendSocket[]::new);
		/** 等待回复字典 */
		public IntSet waitReSet=new IntSet();
		
		public int type;
		
		public SocketSendInfoDic(int type)
		{
			this.type=type;
		}
		
		public void add(int id,SendSocket socket)
		{
			sendDic.put(id,socket);
			waitReSet.add(id);
		}
		
		public boolean has(int id)
		{
			return sendDic.contains(id);
		}
	}
	
	//快捷方式
	/** 中心服连接 */
	public BaseSocket getCenterSocket()
	{
		return getSocketInfo(SocketType.Center).onlySocket;
	}
	
	//快捷方式
	/** 中心服连接 */
	public BaseSocket getManagerSocket()
	{
		return getSocketInfo(SocketType.Manager).onlySocket;
	}
	
	/** 获取game服连接 */
	public BaseSocket getGameSocket(int id)
	{
		return getServerSocket(SocketType.Game,id);
	}
	
	/** 获取登陆服连接 */
	public BaseSocket getLoginSocket(int loginID)
	{
		return getServerSocket(SocketType.Login,loginID);
	}
}
