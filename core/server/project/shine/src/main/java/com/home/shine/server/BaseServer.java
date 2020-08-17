package com.home.shine.server;

import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.constlist.NetWatchType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.DateControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseResponse;
import com.home.shine.net.http.HttpReceive;
import com.home.shine.net.http.ServerHttp;
import com.home.shine.net.httpResponse.BaseHttpResponse;
import com.home.shine.net.httpResponse.BytesHttpResponse;
import com.home.shine.net.response.SocketReconnectResponse;
import com.home.shine.net.socket.BaseSocket;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.SendSocket;
import com.home.shine.net.socket.ServerSocket;
import com.home.shine.net.socket.WebSendSocket;
import com.home.shine.net.webSocket.WebServerSocket;
import com.home.shine.support.collection.SList;
import com.home.shine.tool.DataMaker;
import com.home.shine.tool.IHttpResponseMaker;
import com.home.shine.tool.MessageBindTool;
import com.home.shine.tool.generate.ShineRequestMaker;
import com.home.shine.tool.generate.ShineResponseMaker;
import com.home.shine.utils.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** 服务器连接基类 */
public class BaseServer
{
	/** 连接ID生成器(socket) */
	private AtomicInteger _socketAtomic=new AtomicInteger(1);
	/** httpID生成器(http) */
	private AtomicInteger _httpIDAtomic=new AtomicInteger(1);
	
	/** 服务器长连 */
	private ServerSocket _serverSocket;
	/** 客户端长连 */
	private ServerSocket _clientSocket;
	
	/** 服务器短连 */
	private ServerHttp _serverHttp;
	/** 客户端短连 */
	private ServerHttp _clientHttp;
	
	/** socket字典(需要线程共享,所以不能用实值型字典) */
	private final ConcurrentHashMap<Integer,BaseSocket> _socketDic=new ConcurrentHashMap<>();
	/** http响应字典(需要线程共享,所以不能用实值型字典) */
	private final ConcurrentHashMap<Integer,BaseHttpResponse> _httpResponseDic=new ConcurrentHashMap<>();
	/** 重复http缓存 */
	private final ConcurrentHashMap<String,Boolean> _httpRepeatCacheDic=new ConcurrentHashMap<>();
	
	/** 是否可接客户端消息 */
	private volatile boolean _clientReady=false;
	/** 是否可接服务器消息 */
	private volatile boolean _serverReady=false;
	
	/** 是否运行中 */
	private volatile boolean _running=true;
	
	private int _frameIndex=-1;
	private int _frameIndex2=-1;
	
	//maker
	/** 客户端http协议构造组 */
	private IHttpResponseMaker _clientHttpResponseMaker;
	/** 服务器http协议构造组 */
	private IHttpResponseMaker _serverHttpResponseMaker;
	
	/** 客户端协议构造组 */
	private DataMaker _clientResponseMaker=new DataMaker();
	/** 服务器协议构造组 */
	private DataMaker _serverResponseMaker=new DataMaker();
	/** 客户端bhttp协议构造组 */
	private DataMaker _clientBytesHttpResponseMaker=new DataMaker();
	/** 服务器bhttp协议构造组 */
	private DataMaker _serverBytesHttpResponseMaker=new DataMaker();
	/** 客户端发送消息绑定(机器人用) */
	private MessageBindTool _clientRequestBindTool=new MessageBindTool();
	/** 客户端接收消息绑定(服务器用) */
	private MessageBindTool _clientResponseBindTool=new MessageBindTool();
	
	//count
	
	public BaseServer()
	{
		//启动httpRepeatCache清空
	}
	
	public void init()
	{
		_frameIndex=ThreadControl.getMainTimeDriver().setInterval(this::onSecond,1000);
		_frameIndex2=ThreadControl.getMainTimeDriver().setInterval(this::onMinute,60000);
		
		initMessage();
	}
	
	/** 构造接收器组 */
	protected void initMessage()
	{
		addRequestMaker(new ShineRequestMaker());
		addClientResponseMaker(new ShineResponseMaker());
		addServerResponseMaker(new ShineResponseMaker());
	}
	
	public void dispose()
	{
		if(!_running)
		{
			return;
		}
		
		_running=false;
		
		if(_frameIndex!=-1)
		{
			ThreadControl.getMainTimeDriver().clearInterval(_frameIndex);
			_frameIndex=-1;
		}
		
		if(_frameIndex2!=-1)
		{
			ThreadControl.getMainTimeDriver().clearInterval(_frameIndex2);
			_frameIndex2=-1;
		}
		
		setClientReady(false);
		setServerReady(false);
		
		//socket全关,包括GM
		SList<BaseSocket> list=new SList<>(_socketDic.values());
		
		for(int i=list.size() - 1;i >= 0;--i)
		{
			list.get(i).close();
		}
		
		//http
		
		if(_serverSocket!=null)
		{
			_serverSocket.close();
		}
		
		if(_clientSocket!=null)
		{
			_clientSocket.close();
		}
		
		if(_serverHttp!=null)
		{
			_serverHttp.close();
		}
		
		if(_clientHttp!=null)
		{
			_clientHttp.close();
		}
	}
	
	/** 每秒 */
	private void onSecond(int delay)
	{
		_httpRepeatCacheDic.clear();
	}
	
	/** 每份 */
	private void onMinute(int delay)
	{
		if(!_httpResponseDic.isEmpty())
		{
			long now=DateControl.getTimeMillis();
			
			//遍历删除
			_httpResponseDic.forEachValue(0,v->
			{
				if(v.addTime<=0 || (v.addTime-now)>=ShineSetting.httpRequestKeepTime)
				{
					v.addTime=0;
					_httpResponseDic.remove(v.httpInstanceID);
				}
			});
		}
	
	}
	
	/** 设置客户端消息开关 */
	public void setClientReady(boolean bool)
	{
		_clientReady=bool;
	}
	
	/** 设置服务端消息开关 */
	public void setServerReady(boolean bool)
	{
		_serverReady=bool;
	}
	
	/** 是否运行中 */
	protected boolean isRunning()
	{
		return _running;
	}
	
	//---socket---//
	
	/** 创建客户端接收链接 */
	protected ReceiveSocket toCreateClientReceiveSocket()
	{
		return new ReceiveSocket();
	}
	
	/** 通过连接ID获取连接 */
	public BaseSocket getSocket(int socketID)
	{
		BaseSocket socket=_socketDic.get(socketID);
		
		if(socket==null)
		{
			//这个很可能发生
			Ctrl.warnLog("未找到ID为",socketID,"的连接");
		}
		
		return socket;
	}
	
	/** 新接收连接(io线程) */
	protected void onNewSocket(BaseSocket socket)
	{
		socket.id=_socketAtomic.getAndIncrement();
		
		addSocket(socket);
		
		//		Ctrl.log("新连接进入",socket.id);
	}
	
	/** 新客户端连接(io线程) */
	protected void onNewClientSocket(BaseSocket socket)
	{
		if(ShineSetting.needDebugLog)
			Ctrl.debugLog("客户端新连接",socket.id,"ip:",socket.remoteIP(),"port:",socket.remotePort());
	}
	
	/** 收到一个协议包(IO线程)(跟SendSocket不重名) */
	protected void onSocketDataT(BaseSocket socket,byte[] data,boolean isClient)
	{
		BytesReadStream stream=socket.getReadStream();
		stream.setBuf(data);
		
		//先读协议ID
		int mid=stream.natureReadUnsignedShort();
		
		stream.setReadLenLimit(ShineSetting.getMsgBufSize(isClient,false));
		
		if(ShineSetting.needThreadWatch)
		{
			//观测
			WatchControl.netThreadWatchs[socket.ioIndex].datas[isClient ? NetWatchType.ClientReceive : NetWatchType.ServerReceive].addOne(mid,data.length+ BytesWriteStream.getLenSize(data.length));
		}
		
		//是否需要统计
		
		//检测序号
		if(!BytesControl.isIgnoreMessage(mid) && socket.getOpenIndexCheck())
		{
			int receiveIndex=stream.natureReadShort();
			
			int nowIndex=socket.getReceiveMsgIndex();
			
			if(receiveIndex!=nowIndex)
			{
				Ctrl.warnLog("序号检测没对上,socketID:"+socket.id+" nowIndex:"+nowIndex+" receiveIndex:"+receiveIndex+" mid:"+mid,socket.getInfo());
				
				onClientSocketErrorClose(socket);
				
				return;
			}
			
			socket.addReceiveMsgIndex();
		}
		
		if(isClient ? _clientReady : _serverReady)
		{
			executeMsg(socket,mid,stream,isClient);
		}
		else
		{
			if(!ShineSetup.isExiting())
			{
				//未ready的时候收到消息
				Ctrl.warnLog("未ready的时候收到消息");
			}
			
			//socket.close();
		}
	}
	
	/** 执行消息(IO线程) */
	//关注点  接收协议请求
	private void executeMsg(BaseSocket socket,int mid,BytesReadStream stream,boolean isClient)
	{
		BaseResponse response;
		
		if(isClient)
		{
			response=createClientResponse(mid);
		}
		else
		{
			response=createServerResponse(mid);
		}
		
		if(response==null)
		{
			if(ShineSetting.needMessageExistCheck)
			{
				Ctrl.errorLog("未解析mid为" + mid + "的协议");
			}
			
			return;
		}
		
		response.setSocket(socket);
		
		if(mid==SocketReconnectResponse.dataID)
		{
			((SocketReconnectResponse)response).server=this;
		}
		
		//重设读取限制
		stream.setReadLenLimit(ShineSetting.getMsgBufSize(isClient,response.isLongMessage()));
		
		BaseResponse response2=response.readFromStream(stream,this);
		
		//变了
		if(response2!=response)
		{
			//直接回收
			BytesControl.preReleaseResponse(response);
		}
		
		if(response2!=null)
		{
			//客户端消息必定显示
			if(!BytesControl.isIgnoreMessage(mid) && (ShineSetting.needShowMessage || (isClient && ShineSetting.needShowClientMessage)))
			{
				Ctrl.debugLog("收消息:",socket.getInfo(),"mid:"+mid,ShineSetting.needShowMessageDetail ? response2.toDataString() : response2.getDataClassName());
			}
			
			//需要回执
			if(isClient && _clientResponseBindTool.needReceipt(mid))
			{
				response2.setNeedReceipt(true);
			}
			
			if(isClient)
			{
				dispatchClientResponse(socket,response2);
			}
			else
			{
				response2.dispatch();
			}
		}
	}
	
	/** 派发客户端消息 */
	protected void dispatchClientResponse(BaseSocket socket,BaseResponse response)
	{
		response.dispatch();
	}
	
	/** 客户端连接断开(io线程) */
	protected void onClientSocketClosed(BaseSocket socket)
	{
		toSocketClosed(socket);
	}
	
	/** 服务器连接断开(io线程) */
	protected void onServerSocketClosed(BaseSocket socket)
	{
		toSocketClosed(socket);
	}
	
	/** 添加socket */
	public void addSocket(BaseSocket socket)
	{
		_socketDic.put(socket.id,socket);
	}
	
	private void toSocketClosed(BaseSocket socket)
	{
		_socketDic.remove(socket.id);
	}
	
	/** 客户端连接非法关闭处理(IO线程) */
	protected void onClientSocketErrorClose(BaseSocket socket)
	{
		//视为被动关闭
		socket.closeForIO(BaseSocket.Close_Error);
	}
	
	/** 发送连接连接成功(io线程) */
	protected void onSendConnectSuccess(SendSocket socket)
	{
		
	}
	
	protected void onSendConnectFailed(SendSocket socket)
	{
	
	}
	
	//msg
	
	//启动
	
	protected SendSocket toCreateSendSocket(boolean isClient,boolean isWebSocket)
	{
		if(isWebSocket)
		{
			return new WebSendSocket()
			{
				@Override
				protected void onSocketData(byte[] bb)
				{
					onSocketDataT(this,bb,isClient);
				}
				
				@Override
				protected void onClose()
				{
					if(isClient)
						onClientSocketClosed(this);
					else
						onServerSocketClosed(this);
				}
				
				@Override
				protected void onConnectSuccess()
				{
					onNewSocket(this);
					
					onSendConnectSuccess(this);
				}
				
				@Override
				public void onConnectFailed()
				{
					onSendConnectFailed(this);
				}
			};
		}
		else
		{
			return new SendSocket()
			{
				@Override
				protected void onSocketData(byte[] bb)
				{
					onSocketDataT(this,bb,isClient);
				}
				
				@Override
				protected void onClose()
				{
					if(isClient)
						onClientSocketClosed(this);
					else
						onServerSocketClosed(this);
				}
				
				@Override
				protected void onConnectSuccess()
				{
					onNewSocket(this);
					
					onSendConnectSuccess(this);
				}
				
				@Override
				public void onConnectFailed()
				{
					onSendConnectFailed(this);
				}
			};
		}
	}
	
	/** 创建新的连接(通过tryConnect) */
	protected SendSocket createSendSocket(int sendID,int type,boolean isClient)
	{
		return createSendSocket(sendID,type,isClient,false);
	}
	
	/** 创建新的连接(通过tryConnect) */
	protected SendSocket createSendSocket(int sendID,int type,boolean isClient,boolean isWebSocket)
	{
		SendSocket socket=toCreateSendSocket(isClient,isWebSocket);
		socket.setIsClient(isClient);
		socket.setIsWebSocket(isWebSocket);
		socket.sendID=sendID;
		socket.setType(type);
		return socket;
	}
	
	/** 开启一个clientPort的serverSocket */
	public void startServerSocket(int port)
	{
		_serverSocket=new ServerSocket(port)
		{
			@Override
			public void onReceiveSocketData(ReceiveSocket socket,byte[] data)
			{
				onSocketDataT(socket,data,false);
			}
			
			@Override
			public void onReceiveSocketClose(ReceiveSocket socket)
			{
				onServerSocketClosed(socket);
			}
			
			@Override
			public void onNewReceiveSocket(ReceiveSocket socket)
			{
				onNewSocket(socket);
			}
		};
		
		_serverSocket.setIsClient(false);
		
		_serverSocket.start();
	}
	
	/** 创建客户端ServerSocket */
	protected ServerSocket createClientServerSocket(int port,boolean isWebSocket)
	{
		if(isWebSocket)
		{
			return new WebServerSocket(port)
			{
				@Override
				public ReceiveSocket createReceiveSocket()
				{
					return toCreateClientReceiveSocket();
				}
				
				@Override
				public void onReceiveSocketData(ReceiveSocket socket,byte[] data)
				{
					onSocketDataT(socket,data,true);
				}
				
				@Override
				public void onReceiveSocketClose(ReceiveSocket socket)
				{
					onClientSocketClosed(socket);
				}
				
				@Override
				public void onNewReceiveSocket(ReceiveSocket socket)
				{
					//客户端开双检测
					onNewSocket(socket);
					onNewClientSocket(socket);
				}
			};
		}
		else
		{
			return new ServerSocket(port)
			{
				@Override
				public ReceiveSocket createReceiveSocket()
				{
					return toCreateClientReceiveSocket();
				}
				
				@Override
				public void onReceiveSocketData(ReceiveSocket socket,byte[] data)
				{
					onSocketDataT(socket,data,true);
				}
				
				@Override
				public void onReceiveSocketClose(ReceiveSocket socket)
				{
					onClientSocketClosed(socket);
				}
				
				@Override
				public void onNewReceiveSocket(ReceiveSocket socket)
				{
					//客户端开双检测
					onNewSocket(socket);
					onNewClientSocket(socket);
				}
			};
		}
	}
	
	/** 开启一个clientPort的serverSocket */
	public void startClientSocket(int port,boolean isWebSocket)
	{
		_clientSocket=createClientServerSocket(port,isWebSocket);
		_clientSocket.setIsClient(true);
		_clientSocket.start();
	}
	
	//---http---//
	
	/** 获取一条协议 */
	public BaseHttpResponse getHttpResponse(int httpInstanceID)
	{
		BaseHttpResponse response=_httpResponseDic.get(httpInstanceID);
		
		if(response==null)
			return null;
		
		if(response.removed)
			return null;
		
		return response;
	}
	
	/** 获取一条二进制协议 */
	public BytesHttpResponse getBytesHttpResponse(int httpInstanceID)
	{
		return (BytesHttpResponse)getHttpResponse(httpInstanceID);
	}
	
	/** 移除http消息(只能由httpResponse本体调用) */
	public void removeHttpResponse(int instanceID)
	{
		_httpResponseDic.remove(instanceID);
	}
	
	/** 接受http数据(IO线程) */
	protected void onServerHttpData(HttpReceive receive,boolean isClient)
	{
		//安全策略文件
		if(receive.path.startsWith("crossdomain.xml"))
		{
			receive.result(CrossDomainServer.crossDomain,HttpContentType.Text);
			
			return;
		}
		
		//图标
		if(receive.path.startsWith("favicon.ico"))
		{
			receive.result("",HttpContentType.Text);
			return;
		}
		
		if(isClient)
		{
			if(!_clientReady)
			{
				receive.dispose();
				return;
			}
		}
		else
		{
			if(!_serverReady)
			{
				receive.dispose();
				return;
			}
		}
		
		String cmd=getHttpCmd(receive);
		
		BaseHttpResponse response=null;
		
		int mid=0;
		
		//二进制
		if(cmd.equals(ShineSetting.bytesHttpCmd) && receive.method==HttpMethodType.Post)
		{
			receive.postStream.setReadLenLimit(ShineSetting.getMsgBufSize(isClient,false));
			
			//协议ID
			mid=receive.postStream.natureReadUnsignedShort();
			
			if(isClient)
			{
				response=createClientBytesHttpResponse(mid);
			}
			else
			{
				response=createServerBytesHttpResponse(mid);
			}
		}
		else
		{
			if(isClient)
			{
				response=makeClientHttpResponse(cmd,receive);
			}
			else
			{
				response=makeServerHttpResponse(cmd,receive);
			}
		}
		
		if(response==null)
		{
			noneHttpResponse(receive,cmd,mid);
			
			//关了
			receive.dispose();
			return;
		}
		
		//initDebug
		
		response.init(this,receive);
		
		response.url=receive.url;
		response.cmd=cmd;
		
		if(response.igorneRepeated())
		{
			//已存在
			if(_httpRepeatCacheDic.containsKey(response.url))
			{
				Ctrl.warnLog("重复http",response.url);
				
				response.dispose();
				return;
			}
			
			_httpRepeatCacheDic.put(response.url,true);
		}

		//不是bytes协议
		if(!response.cmd.equals(ShineSetting.bytesHttpCmd))
		{
			//get
			if(receive.method==HttpMethodType.Get)
			{
				response.args=receive.args;
			}
			else if(receive.method==HttpMethodType.Post)
			{
				response.args=receive.args;
				//默认json方式
				response.jsonArgs=StringUtils.getJsonArgs(receive.getPostStr());
			}
		}
		
		boolean hasError=false;
		
		try
		{
			response.preRead();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			hasError=true;
		}

		//合法
		if(!hasError && checkHttpResponseEx(response))
		{
			toDispatchHttpResponse(response);
		}
		else
		{
			response.dispose();
		}
	}
	
	protected void toDispatchHttpResponse(BaseHttpResponse response)
	{
		response.httpInstanceID=_httpIDAtomic.getAndIncrement();
		response.addTime=DateControl.getTimeMillis();
		_httpResponseDic.put(response.httpInstanceID,response);
		
		response.dispatch();
	}
	
	/** 获取http协议号 */
	protected String getHttpCmd(HttpReceive receive)
	{
		return receive.path;
	}
	
	/** 空消息处理 */
	protected void noneHttpResponse(HttpReceive receive,String cmd,int mid)
	{
		if(ShineSetting.needMessageExistCheck)
		{
			Ctrl.errorLog("未解析cmd为" + cmd + "的http协议,mid="+mid);
		}
	}

	/** 额外检查httpResponse(留给g层做权限用) */
	protected boolean checkHttpResponseEx(BaseHttpResponse response)
	{
		return true;
	}
	
	/** 通过协议号构造响应(客户端) */
	protected BaseHttpResponse makeClientHttpResponse(String cmd,HttpReceive receive)
	{
		if(_clientHttpResponseMaker==null)
		{
			return null;
		}
		
		BaseHttpResponse response=_clientHttpResponseMaker.getHttpResponseByID(cmd);
		
		if(response!=null)
		{
			return response;
		}
		
		return null;
	}
	
	/** 通过协议号构造响应(服务器) */
	private BaseHttpResponse makeServerHttpResponse(String cmd,HttpReceive receive)
	{
		////刷新
		//if(cmd.equals("agent"))
		//{
		//	return new AgentHttpResponse();
		//}
		
		if(_serverHttpResponseMaker==null)
		{
			return null;
		}
		
		BaseHttpResponse response=_serverHttpResponseMaker.getHttpResponseByID(cmd);
		
		if(response!=null)
		{
			return response;
		}
		
		return null;
	}
	
	//启动
	/** 开启一个serverPort的serverHttp */
	public ServerHttp startServerHttp(int port)
	{
		return startServerHttp(port,false);
	}
	
	/** 开启一个serverPort的serverHttp */
	public ServerHttp startServerHttp(int port,boolean needHttps)
	{
		_serverHttp=new ServerHttp(port)
		{
			@Override
			public void onStartFailed()
			{
				Ctrl.warnLog("服务器http端口启动失败:",port, ShineGlobal.processName);
			}
			
			@Override
			public void onHttpReceive(HttpReceive receive)
			{
				onServerHttpData(receive,false);
			}
		};
		
		_serverHttp.setIsClient(false);
		if(needHttps)
		{
			_serverHttp.setIsHttps(true);
		}
		
		_serverHttp.start();
		
		return _serverHttp;
	}
	
	/** 开启一个clientPort的serverHttp */
	public ServerHttp startClientHttp(int port)
	{
		_clientHttp=new ServerHttp(port)
		{
			@Override
			public void onStartFailed()
			{
				Ctrl.warnLog("客户端http端口启动失败:",port);
			}
			
			@Override
			public void onHttpReceive(HttpReceive receive)
			{
				onServerHttpData(receive,true);
			}
		};
		
		_clientHttp.setIsClient(true);
		
		if(ShineSetting.clientHttpUseHttps)
		{
			_clientHttp.setIsHttps(true);
		}
		
		_clientHttp.start();
		
		return _clientHttp;
	}
	
	//create
	
	/** 通过协议号构造响应(客户端) */
	public BaseResponse createClientResponse(int mid)
	{
		if(ShineSetting.messageUsePool)
		{
			if(!_clientResponseMaker.contains(mid))
				return null;
			
			return BytesControl.createResponse(mid);
		}
		else
		{
			return (BaseResponse)_clientResponseMaker.getDataByID(mid);
		}
	}
	
	/** 通过协议号构造响应(服务器) */
	public BaseResponse createServerResponse(int mid)
	{
		if(ShineSetting.messageUsePool)
		{
			if(!_serverResponseMaker.contains(mid))
				return null;
			
			return BytesControl.createResponse(mid);
		}
		else
		{
			return (BaseResponse)_serverResponseMaker.getDataByID(mid);
		}
	}
	
	/** 通过协议号构造二进制响应(客户端) */
	private BaseHttpResponse createClientBytesHttpResponse(int mid)
	{
		return (BaseHttpResponse)_clientBytesHttpResponseMaker.getDataByID(mid);
	}
	
	/** 通过协议号构造二进制响应(服务器) */
	private BaseHttpResponse createServerBytesHttpResponse(int mid)
	{
		return (BaseHttpResponse)_serverBytesHttpResponseMaker.getDataByID(mid);
	}
	
	//maker
	
	/** 添加客户端http响应构造器 */
	public void setClientHttpResponseMaker(IHttpResponseMaker maker)
	{
		_clientHttpResponseMaker=maker;
	}
	
	/** 添加服务器http响应构造器 */
	public void setServerHttpResponseMaker(IHttpResponseMaker maker)
	{
		_serverHttpResponseMaker=maker;
	}
	
	/** 添加发送消息构造 */
	public void addRequestMaker(DataMaker maker)
	{
		BytesControl.addRequestMaker(maker);
	}
	
	/** 添加客户端响应构造器 */
	public void addClientResponseMaker(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
		_clientResponseMaker.addDic(maker);
	}
	
	/** 添加服务器响应构造器 */
	public void addServerResponseMaker(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
		_serverResponseMaker.addDic(maker);
	}
	
	/** 添加客户端bhttp响应构造器 */
	public void addClientBytesHttpResponseMaker(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
		_clientBytesHttpResponseMaker.addDic(maker);
	}
	
	/** 添加服务器bhttp响应构造器 */
	public void addServerBytesHttpResponseMaker(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
		_serverBytesHttpResponseMaker.addDic(maker);
	}
	
	/** 添加客户端接收消息绑定 */
	public void addClientResponseBind(MessageBindTool tool)
	{
		_clientResponseBindTool.addDic(tool);
	}
	
	/** 添加客客户端发送消息绑定 */
	public void addClientRequestBind(MessageBindTool tool)
	{
		_clientRequestBindTool.addDic(tool);
	}
	
	//为client补
	
	/** 直接设置客户端响应构造(省内存) */
	public void setClientResponseMaker(DataMaker maker)
	{
		_clientResponseMaker=maker;
	}
}
