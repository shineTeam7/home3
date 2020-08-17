using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace ShineEngine
{
	/// <summary>
	/// socket基类
	/// </summary>
	public class BaseSocket
	{
		/** 关闭的 */
		public const int Closed=1;
		/** 连接中 */
		public const int Connecting=2;
		/** 已连接 */
		public const int Connected=3;


		/** 主动关闭 */
		public static int Close_Initiative=1;
		/** 被动-网络断开 */
		public static int Close_ChannelInactive=2;
		/** 被动-重连失败 */
		public static int Close_ReconnectFailed=3;
		/** 被动-解析出错 */
		public static int Close_Error=4;
		/** 被动-超时 */
		public static int Close_TimeOut=5;
		/** 被动-闪断测试 */
		public static int Close_Test=6;


		private static int _connectTimeMax=10;//10s

		//--base--//

		private BaseServer _server;

		/** 当前状态(主线程写,IO线程读) */
		private volatile int _state=Closed;
		/** 执行索引 */
		private volatile int _doIndex=0;

		/** ip信息记录 */
		private SMap<string,IPAddressInfo> _ipInfoDic=new SMap<string,IPAddressInfo>();
		private string _host;
		private int _port;

		private IPAddressInfo _currentInfo;
		/** 目标地址 */
		private IPAddress _hostAddress;
		/** 是否析构了 */
		private volatile bool _disposed=false;


		/** 连接计时 */
		private int _connectTime=0;

		private int _secondLastTime=0;

		/** 发队列 */
		private SBatchQueue<BaseRequest> _sendQueue;
		/** 收队列(同步异步都用) */
		private SBatchQueue<BaseResponse> _receiveQueue;
		/** 消息缓存队列 */
		private SQueue<BaseResponse> _responseCacheQueue;

		//--udp--//


		//--tcp--//

		/** 连接内容 */
		private BaseSocketContent _content;

		/** 发送线程 */
		private BaseThread _sendThread;
		/** 接收线程(阻塞) */
		private BaseThread _receiveThread;
		/** kcp线程 */
		private BaseThread _kcpThread;

		//--other--//

		//ping
		/** 心跳时间经过(秒) */
		private int _pingTimePass=0;

		/** 已发送ping包 */
		protected bool _pingSending=false;
		/** ping序号 */
		protected int _pingIndex=0;
		/** 发送ping时间 */
		protected long _sendPingTime=0;
		/** 当前网络延迟(ms) */
		private int _ping=-1;

		//reconnect
		/** 是否允许断线重连 */
		private bool _openReconnect=false;
		/** 是否断线重连中 */
		private bool _reconnecting=false;
		/** 重连剩余时间(秒) */
		private int _reconnectLastTime=0;

		/** 两次连接尝试的间隔(3秒) */
		private AffairTimeOut _tryConnectTime;
		/** 重连间隔(3秒) */
		private AffairTimeOut _reConnectTime;

		private bool _closeByInitiative;

		/** 接收连接ID */
		private int _receiveID=-1;
		/** 接收令牌 */
		private int _receiveToken=-1;

		/** 读流(io线程操作) */
		private BytesReadStream _readStream=new BytesReadStream();
		/** 写流(主线程操作) */
		private BytesWriteStream _writeStream=new BytesWriteStream();
		/** 写流发送标记(异步用) */
		private int _writeStreamSendPos=0;

		// /** 是否无限尝试连接 */
		// private bool _isTryConnect=false;
		//try
		/** 当前尝试次数(-1就是未工作) */
		private int _currentTryCount;
		/** 最多尝试次数 */
		private int _tryCountMax=0;

		//连接成功回调
		private Action _connectCall;

		//连接失败回调
		private Action _connectFailedCall;

		//连接被关闭回调
		private Action _closeCall;

		//构造协议回调
		private Func<int,BaseResponse> _createResponseFunc;

		//check

		/** 发送序号 */
		private short _sendMsgIndex=0;

		private short _sendCheckIndex=0;
		/** 接收协议序号 */
		private short _receiveMsgIndex=0;

		/** 发送消息缓存队列 */
		private BaseRequest[] _sendCacheRequestQueue;
		/** 断线时发送的序号 */
		private short _disConnectLastSendIndex=0;

		//temp
		private int _newResponseNum=0;

		public BaseSocket()
		{
			_sendQueue=new SBatchQueue<BaseRequest>(toSendOne);
			_receiveQueue=new SBatchQueue<BaseResponse>(toRunResponseOne);

			_sendCacheRequestQueue=new BaseRequest[ShineSetting.requestCacheLen];

			_reConnectTime=new AffairTimeOut(reconnectTimeOut,3);

			_tryConnectTime=new AffairTimeOut(retryConnect,1);//1秒就重试
		}

		public void setServer(BaseServer server)
		{
			_server=server;
		}

		public void init()
		{
			if(ShineSetting.messageUsePool)
			{
				_responseCacheQueue=new SQueue<BaseResponse>();
				_readStream.setDataPool(BytesControl.netDataPool);
			}

			startSocketThread();
		}

		/** 析构 */
		public void dispose()
		{
			closeAndClear();

			_disposed=true;

			if(ShineSetting.useKCP)
			{
				if(_kcpThread!=null)
				{
					_kcpThread.exit();
					ThreadControl.removeThread(_kcpThread);
					_kcpThread=null;
				}
			}


			if(!ShineSetting.socketUseAsync)
			{
				if(_sendThread!=null)
				{
					_sendThread.exit();
					ThreadControl.removeThread(_sendThread);
					_sendThread=null;
				}

				if(_receiveThread!=null)
				{
					_receiveThread.exit();
					ThreadControl.removeThread(_receiveThread);
					_receiveThread=null;
				}
			}
		}

		/// <summary>
		/// 连接回调
		/// </summary>
		public void setConnectCall(Action func)
		{
			_connectCall=func;
		}

		/// <summary>
		/// 连接失败回调
		/// </summary>
		public void setConnectFailedCall(Action func)
		{
			_connectFailedCall=func;
		}

		/// <summary>
		/// 连接被关闭回调
		/// </summary>
		public void setCloseCall(Action func)
		{
			_closeCall=func;
		}

		/// <summary>
		/// 构造协议回调
		/// </summary>
		public void setCreateResponseFunc(Func<int,BaseResponse> func)
		{
			_createResponseFunc=func;
		}

		/** 清空(回归) */
		public void clear()
		{
			_sendQueue.clear();
			_writeStream.clear();
			_writeStreamSendPos=0;

			//序号归零
			_sendMsgIndex=0;
			_sendCheckIndex=0;
			_receiveMsgIndex=0;
			_reconnecting=false;
			_reconnectLastTime=0;
			_pingTimePass=0;
			_ping=-1;
			_pingSending=false;

			_receiveID=-1;
			_receiveToken=-1;
		}

		private void clearSend()
		{
			_sendQueue.clear();
			_writeStream.clear();
		}

		private void clearAddress()
		{
			_currentInfo=null;
			_hostAddress=null;
		}

		/// <summary>
		/// 每帧
		/// </summary>
		public void onFrame(int delay)
		{
			_secondLastTime+=delay;

			if(_secondLastTime>=1000)
			{
				//归零不累计
				_secondLastTime=0;

				onSecond();
			}

			_receiveQueue.runOnce();

			sendAsyncOnce();
		}

		/** 是否开启断线重连 */
		public void setOpenReconnect(bool value)
		{
			_openReconnect=value;
		}

		public bool isOpenReconnect()
		{
			return _openReconnect;
		}

		/** 是否连接着对逻辑而言 */
		public bool isConnectForLogic()
		{
			return _reconnecting || _state==Connected;
		}

		/** 是否连接着 */
		public bool isConnect()
		{
			return _state==Connected;
		}

		/** 是否正在连接中 */
		public bool isConnecting()
		{
			return _state==Connecting;
		}

		/** 是否断线重连中 */
		public bool isReconnecting()
		{
			return _reconnecting;
		}

		/** 获取状态 */
		public int getState()
		{
			return _state;
		}

		public int getDoIndex()
		{
			return _doIndex;
		}

		/** 获取当前ping(网络延迟)(一来一回,包括IO线程消耗，不包括切主线程) */
		public int getPing()
		{
			return _ping;
		}

		/** 设置接收连接信息(主线程) */
		public void setReceiveInfo(int receiveID,int token)
		{
			//已有
			if(_receiveID>0)
				return;

			_receiveID=receiveID;
			_receiveToken=token;
		}

		/** 连接 */
		public void connect(string host,int port)
		{
			//连接尝试3次
			_tryCountMax=3;
			preConnect(host,port);
		}

		/** 重新尝试连接 */
		public void reConnect()
		{
			// connect(_host,_port);
			preConnectAfterAddress();
		}

		private void preConnect(string host,int port)
		{
			close();

			_host=host;
			_port=port;

			toGetDNS();
		}

		private void toGetDNS()
		{
			_state=Connecting;
			_connectTime=0;

			int index=++_doIndex;

			_currentInfo=_ipInfoDic.get(_host);

			if(_currentInfo!=null)
			{
				preConnectAfterAddress();
			}
			else
			{
				IPAddress address;

				if(!IPAddress.TryParse(_host,out address))
				{
					Ctrl.debugLog("dns上");

					Dns.BeginGetHostAddresses(_host,v=>
					{
						IPAddress[] addresses=null;

						try
						{
							addresses=Dns.EndGetHostAddresses(v);
						}
						catch(Exception e)
						{
							Ctrl.printExceptionForIO(e);
						}

						ThreadControl.addMainFunc(()=>
						{
							if(index==_doIndex)
							{
								if(addresses!=null)
								{
									foreach(IPAddress ipAddress in addresses)
									{
										Ctrl.print("dns看收到的上地址",ipAddress);
									}

									_currentInfo=registIPInfo(_host,addresses);
									preConnectAfterAddress();
								}
								else
								{
									Ctrl.printForIO("解析dns失败");
								}
							}
							else
							{
								Ctrl.print("dns获取完，已经到下个index");
							}
						});
					},null);
				}
				else
				{
					Ctrl.debugLog("dns下");
					_currentInfo=registIPInfo(_host,new[] {address});
					preConnectAfterAddress();
				}
			}
		}

		private IPAddressInfo registIPInfo(string host,IPAddress[] addresses)
		{
			IPAddressInfo info=new IPAddressInfo();
			info.host=host;
			info.parse(addresses);
			_ipInfoDic.put(host,info);

			return info;
		}

		/** 创建新连接(主线程) */
		private void createNewContent()
		{
			++_doIndex;

			if(ShineSetting.useKCP)
			{
				_content=new KcpSocketContent(this,_doIndex);
			}
			else
			{
				_content=new TCPSocketContent(this,_doIndex);
			}
		}

		private void preConnectAfterAddress()
		{
			_currentTryCount=0;
			toConnectOnce();
		}

		private void retryConnect()
		{
			toCloseForConnect();
			toConnectOnce();
		}

		/** 连接成功(主线程) */
		public void preConnectSuccessForIO()
		{
			if(!isConnecting())
				return;

			_state=Connected;

			Ctrl.print("socket连接成功:",_doIndex);

			//重连中
			if(_reconnecting && _receiveID>0)
			{
				Ctrl.print("发送重连消息",_receiveID,_receiveToken,_receiveMsgIndex);

				refreshPingTime();

				clearSend();
				sendAbs(SocketReconnectRequest.create(_receiveID,_receiveToken,_receiveMsgIndex));

				sendPingRequest();
				_reConnectTime.start();
			}
			else
			{
				clear();
				//发一次ping
				sendPingRequest();

				onConnectSuccess();
			}
		}

		/** 执行关闭(主线程) */
		private void closeForIO(int reason)
		{
			closeForIO(reason,true);
		}

		/** 执行关闭(主线程) */
		private void closeForIO(int reason,bool canRec)
		{
			ThreadControl.checkCurrentIsMainThread();

			if(_state==Closed)
				return;

			//正在连接中
			if(isConnecting())
			{
				stopConnect();
			}

			_state=Closed;

			Ctrl.print("连接断开,reason:",reason,_doIndex);

			_doIndex++;//序号加

			bool isInitiative=reason==Close_Initiative;

			if(isInitiative)
			{
				toFlush();
			}

			onDisconnect();

			//重连中，会再次重连
			if(_openReconnect && !isInitiative && canRec && canReconnect()) //!_reconnecting &&
			{
				Ctrl.log("连接断线，进入重连阶段");
				_reconnecting=true;
				_disConnectLastSendIndex=_sendMsgIndex;
				_reconnectLastTime=ShineSetting.socketReConnectKeepTime;
				_closeByInitiative=isInitiative;

				clearSend();

				beginReconnect();
			}
			else
			{
				_closeByInitiative=isInitiative;
				realClose();
			}
		}

		/** 开始重连(io线程) */
		protected void beginReconnect()
		{
			_tryCountMax=0;
			reConnect();
		}

		/** 实际关闭(主线程) */
		protected void realClose()
		{
			_receiveID=-1;
			_receiveToken=-1;
			_reConnectTime.stop();

			_reconnecting=false;
			_disConnectLastSendIndex=0;

			BaseRequest request;
			for(int i=0;i<_sendCacheRequestQueue.Length;i++)
			{
				if((request=_sendCacheRequestQueue[i])!=null)
				{
					request.doRelease();
					_sendCacheRequestQueue[i]=null;
				}
			}

			_sendMsgIndex=0;
			_sendCheckIndex=0;
			_receiveMsgIndex=0;

			if(!_closeByInitiative)
			{
				if(_closeCall!=null)
					_closeCall();
			}
		}

		/** 是否可重连 */
		protected bool canReconnect()
		{
			return _receiveID>0;
		}

		/** try到时间(主线程) */
		private void reconnectTimeOut()
		{
			reconnectFailed();
		}

		/** 收到重连失败(主线程) */
		public void onReconnectFailed()
		{
			reconnectFailed();
		}

		/** 重连失败(主线程) */
		protected void reconnectFailed()
		{
			if(!_reconnecting)
				return;

			if(isConnecting())
			{
				stopConnect();
			}

			_closeByInitiative=false;
			realClose();
		}

		/** 停止连接 */
		protected void stopConnect()
		{
			_tryConnectTime.stop();
		}

		private void onDisconnect()
		{
			if(_content!=null)
			{
				_content.close();
				_content=null;
			}

			_reConnectTime.stop();
		}

		private void toCloseForConnect()
		{
			if(_state==Closed)
				return;

			_state=Closed;

			_doIndex++;//序号加

			onDisconnect();
		}

		/** 推送剩余的消息 */
		private void toFlush()
		{
			sendAsyncOnce();
		}

		/** 被关闭(主线程) */
		public void beClose()
		{
			closeForIO(Close_ChannelInactive);
		}

		/// <summary>
		/// 关闭(不调回调)(主线程)
		/// </summary>
		public void close()
		{
			sendAbs(SocketCloseRequest.create());
			closeForIO(Close_Initiative);
			clearAddress();
			stopConnect();
		}

		/** 闪断测试 */
		public void closeTest()
		{
			// closeForIO(Close_Test);
			if(_content!=null)
				_content.closeTest();
		}

		/** 析构 */
		public void closeAndClear()
		{
			close();
			clear();
		}

		private void onSecond()
		{
			if(_reconnecting)
			{
				if(--_reconnectLastTime<=0)
				{
					_reconnectLastTime=0;

					Ctrl.log("重连超时,连接关闭");

					reconnectFailed();
				}
			}

			// Ctrl.print("newResponseNum",_newResponseNum);
			_newResponseNum=0;

			if(ShineSetting.messageUsePool)
			{
				long nowTime=Ctrl.getFixedTimer();
				int key;
				for(short i=_sendCheckIndex;i!=_sendMsgIndex;i++)
				{
					BaseRequest request=_sendCacheRequestQueue[key=(i & ShineSetting.requestCacheMark)];

					if(request==null)
					{
						Ctrl.errorLog("不该找不到消息",i);
						break;
					}
					else
					{
						if((nowTime - request.sendTime)>ShineSetting.requestCacheTime)
						{
							if(!request.sended)
							{
								Ctrl.warnLog("消息还未发送",request.getDataID());
								break;
							}

							request.doRelease();
							_sendCacheRequestQueue[key]=null;
							_sendCheckIndex++;
						}
						else
						{
							break;
						}
					}
				}
			}

			switch(_state)
			{
				case Connected:
				{
					// if(!_socket.Connected)
					// {
					// 	//连接被关闭
					// 	beClose();
					// }

					++_pingIndex;

					if(_pingIndex>=ShineSetting.pingTime)
					{
						_pingIndex=0;

						sendPingRequest();
					}

					if(ShineSetting.needPingCut)
					{
						++_pingTimePass;

						if(_pingTimePass>ShineSetting.pingKeepTime)
						{
							Ctrl.warnLog("连接超时,强制关闭");
							_pingTimePass=0;
							closeForIO(Close_TimeOut);
						}
					}
				}
					break;
				case Connecting:
				{
					//连接超时
					if((++_connectTime)>=_connectTimeMax)
					{
						Ctrl.log("连接中超时");

						_connectTime=0;
						preConnectFailedForIO();
					}
				}
					break;
				case Closed:
				{
					_tryConnectTime.onSecond();
				}
					break;
			}


		}

		/** 主线程 */
		private void toConnectOnce()
		{
			Ctrl.print("connectOnce");

			createNewContent();

			if(_currentInfo==null)
			{
				Ctrl.errorLog("此时不该没有currentInfo");
				toGetDNS();
				return;
			}

			_hostAddress=_currentInfo.getOnce();

			_state=Connecting;
			_connectTime=0;
			++_currentTryCount;

			Ctrl.printForIO("toConnect一次:"+_hostAddress,_content.GetHashCode());

			_content.connect(_hostAddress,_port);
		}

		/** 连接失败一次(主线程) */
		public void preConnectFailedForIO()
		{
			if(!isConnecting())
				return;

			Ctrl.log("socket连接失败一次",_doIndex);

			if(_currentInfo!=null)
			{
				//记录一次失败
				_currentInfo.failOnce();
			}

			toCloseForConnect();

			//超出次数限制
			if(_tryCountMax>0 && _currentTryCount>=_tryCountMax)
			{
				_connectTime=0;

				if(_connectFailedCall!=null)
					_connectFailedCall();
			}
			else
			{
				if(_currentInfo!=null)
				{
					if(_currentInfo.isCircle())
					{
						_tryConnectTime.start();
					}
					else
					{
						//此次不计次数
						--_currentTryCount;
						retryConnect();
					}
				}
				else
				{
					//dns没获取到
					toGetDNS();
				}
			}

		}

		private void startSocketThread()
		{
			Ctrl.print("启动socket线程");

			if(ShineSetting.useKCP)
			{
				_kcpThread=new BaseThread("kcpThread");
				_kcpThread.setRunCall(kcpLoop);
				ThreadControl.addThread(_kcpThread);
				_kcpThread.start();
			}

			if(ShineSetting.socketUseAsync)
				return;

			_sendThread=new BaseThread("ioSendThread");
			_sendThread.setRunCall(sendLoop);
			ThreadControl.addThread(_sendThread);

			_receiveThread=new BaseThread("ioReceiveThread");
			_receiveThread.setRunCall(receiveLoop);
			_receiveThread.setSleepTime(1);
			ThreadControl.addThread(_receiveThread);

			_sendThread.start();
			_receiveThread.start();
		}


		private void onConnectSuccess()
		{
			if(_connectCall!=null)
				_connectCall();
		}

		/** 异步发送一次(主线程) */
		private void sendAsyncOnce()
		{
			if(!ShineSetting.socketUseAsync && !ShineSetting.useKCP)
				return;

			if(!isConnect())
				return;

			//当前没有
			if(_content==null)
				return;

			//还在发送中
			if(_content.isSending())
				return;

			if(_writeStreamSendPos>0)
			{
				_writeStream.removeFront(_writeStreamSendPos);
				_writeStreamSendPos=0;
			}

			if(_writeStream.length()>0)
			{
				_writeStreamSendPos=_writeStream.length();
				_content.sendAsyncOnce(_writeStream.getBuf(),0,_writeStream.length());
			}
		}

		/** 刷ping的时间 */
		public void refreshPingTime()
		{
			_pingTimePass=0;
		}

		public void notifySendThread()
		{
			_sendThread.notifyFunc();
		}

		private void toSendOne(BaseRequest request)
		{
			if(request.released || request.sendMsgIndex==-1)
			{
				Ctrl.warnLog("消息已被释放",request.getDataID());
				return;
			}

			toWriteRequestToStream(_writeStream,request,request.sendMsgIndex);
		}

		/** 执行写入流(主线程) */
		private void doWriteRequestToStream(BaseRequest request,int index)
		{
			request.sendTime=Ctrl.getFixedTimer();
			request.sended=false;

			if(!ShineSetting.useKCP && ShineSetting.socketUseAsync)
			{
				toWriteRequestToStream(_writeStream,request,index);
			}
			else
			{
				request.sendMsgIndex=index;
				_sendQueue.add(request);

				if(ShineSetting.needThreadNotify)
				{
					if(ShineSetting.useKCP)
					{
						_kcpThread.notifyFunc();
					}
					else
					{
						_sendThread.notifyFunc();
					}
				}
			}
		}

		/** 实际执行写request到stream */
		private void toWriteRequestToStream(BytesWriteStream stream,BaseRequest request,int index)
		{
			stream.clearBooleanPos();

			int limit=request.getMessageLimit();
			stream.setWriteLenLimit(limit);
			int mid=request.getDataID();

			int startPos=stream.getPosition();

			//写协议ID(原生写)
			stream.natureWriteUnsignedShort(mid);

			if(!BytesControl.isIgnoreMessage(mid))
			{
				stream.natureWriteShort(index);
			}

			request.writeToStream(stream);

			int endPos=stream.getPosition();
			int len=endPos-startPos;

			if(limit>0 && len>=limit)
			{
				Ctrl.errorLog("request长度超出上限",len);
			}

			if(ShineSetting.needCustomLengthBasedFrameDecoder)
			{
				stream.insertLenToPos(startPos,len);
			}
			else
			{
				stream.setPosition(startPos);

				stream.insertVoidBytes(4,len);

				stream.natureWriteInt(len);

				stream.setPosition(endPos);
			}

			//写完的标记
			request.sended=true;
		}

		/** 重连的下一步(主线程) */
		protected void doReconnectNext(short lastReceiveIndex,bool needReMsg)
		{
			short dd=indexD(_sendMsgIndex,lastReceiveIndex);

			if(dd<0 || dd>=ShineSetting.requestCacheLen)
			{
				Ctrl.warnLog("重连时，消息缓存已失效");

				closeForIO(Close_ReconnectFailed,false);
				return;
			}

			if(needReMsg)
			{
				sendAbs(SocketReconnectSuccessRequest.create(_receiveMsgIndex));
			}

			for(short i=lastReceiveIndex;i!=_sendMsgIndex;i++)
			{
				BaseRequest request=_sendCacheRequestQueue[i & ShineSetting.requestCacheMark];

				if(request==null)
				{
					Ctrl.warnLog("找不到缓存消息,或是超时被释放");
					closeForIO(Close_ReconnectFailed,false);
					return;
				}
				else
				{
					doWriteRequestToStream(request,i);
				}
			}

			_reconnecting=false;
			_disConnectLastSendIndex=0;

			//推送一次
			toFlush();

			Ctrl.log("socket重连成功:");
		}

		private void kcpLoop()
		{
			if(_disposed)
				return;

			if(!isConnect())
				return;

			if(_content==null)
				return;

			_content.onKcpLoop();

		}

		private void sendLoop()
		{
			if(_disposed)
				return;

			if(!isConnect())
				return;

			if(_content==null)
				return;

			if(ShineSetting.useKCP)
			{
				_content.kcpSendLoop();
			}
			else
			{
				runSendQueue();
			}
		}

		/** 发线程或kcp线程 */
		public void runSendQueue()
		{
			BaseSocketContent content=_content;

			if(content!=null && content.checkIsCurrent())
			{
				_sendQueue.runOnce();

				BytesWriteStream writeStream;
				if((writeStream=_writeStream).length()>0)
				{

					content.toSendStream(writeStream);
					writeStream.clear();
				}
			}
		}

		/** 接收循环(io线程) */
		private void receiveLoop()
		{
			if(_disposed)
				return;

			if(!isConnect())
				return;

			if(_content==null)
				return;

			socketTick();
			_content.receiveLoopOnce();
		}

		/** 执行response们(主线程) */
		private void toRunResponseOne(BaseResponse response)
		{
			int mid=response.getDataID();

			if(ShineSetting.needShowMessage && !BytesControl.isIgnoreMessage(mid))
			{
				Ctrl.debugLog("收消息:","mid:"+mid,ShineSetting.needShowMessageDetail ? response.toDataString() : response.getDataClassName());
			}

			_server.checkResponseUnbind(mid);

			try
			{
				response.run();
			}
			catch(Exception e)
			{
				Ctrl.errorLogForIO(e);
			}
		}

		/// <summary>
		/// 发送ping消息
		/// </summary>
		private void sendPingRequest()
		{
			if(_pingSending)
			{
				_ping=-1;
			}

			_pingSending=true;
			_sendPingTime=Ctrl.getTimer();
			sendAbs(PingRequest.create(++_pingIndex));
		}

		//check

		/** 发送到连接 */
		public void send(BaseRequest request)
		{
			send(request,true);
		}

		/** 发送到连接(主线程) */
		public void send(BaseRequest request,bool needLog)
		{
			int mid=request.getDataID();

			if(ShineSetting.needShowMessage && needLog)
			{
				Ctrl.debugLog("发消息:","mid:"+mid,ShineSetting.needShowMessageDetail ? request.toDataString() : request.getDataClassName());
			}

			//可发送 并且消息绑定通过
			if(checkRequestNeedSend(request) && _server.checkRequestBind(mid))
			{
				sendRequestForIO(request);
			}
		}

		/** 发abs消息 */
		public void sendAbs(BaseRequest request)
		{
			request.preSend();

			if(isConnect())
			{
				doWriteRequestToStream(request,0);
			}
		}


		/** 发送request(主线程) */
		protected void sendRequestForIO(BaseRequest request)
		{
			request.preSend();

			bool needIndex=false;
			short index=0;

			if(!BytesControl.isIgnoreMessage(request.getDataID()))
			{
				needIndex=true;
				index=_sendMsgIndex++;

				int key=index & ShineSetting.requestCacheMark;
				//存起
				BaseRequest cache;
				if((cache=_sendCacheRequestQueue[key])!=null)
				{
					cache.doRelease();

					if(ShineSetting.messageUsePool)
					{
						//序号碰到
						if(key==(_sendCheckIndex & ShineSetting.requestCacheMark))
						{
							Ctrl.warnLog("出现一次发送index超过检查index",_sendCheckIndex);
							_sendCheckIndex++;
						}
					}
				}

				_sendCacheRequestQueue[key]=request;
			}

			if(_reconnecting)
			{
				if(needIndex)
				{
					//失效
					if(indexD(index,_disConnectLastSendIndex)>=ShineSetting.requestCacheLen)
					{
						reconnectFailed();
					}
				}
			}
			else
			{
				doWriteRequestToStream(request,index);

			}
		}

		private bool checkRequestNeedSend(BaseRequest request)
		{
			//重连中
			if(_reconnecting)
			{
				//不保留的
				if(!request.needCacheOnDisConnect())
					return false;
			}
			else
			{
				//还未连接
				if(!isConnect())
					return false;
			}

			return true;
		}

		/** 收到一个协议包(IO线程) */
		public void onePiece(byte[] bytes,int pos,int len)
		{
			//更新ping时间
			refreshPingTime();
			onSocketDataT(bytes,pos,len);
		}

		/** 收到一个协议包(netIO线程) */
		private void onSocketDataT(byte[] data,int pos,int len)
		{
			_readStream.setBuf(data,pos,len);

			//客户端不需要检测

			int mid=_readStream.natureReadUnsignedShort();

			//检测序号
			if(!BytesControl.isIgnoreMessage(mid))
			{
				int receiveIndex=_readStream.natureReadShort();

				int nowIndex=_receiveMsgIndex;

				if(receiveIndex!=nowIndex)
				{
					ThreadControl.addMainFunc(()=>
					{
						Ctrl.warnLog("序号检测没对上,"+" nowIndex:"+nowIndex+" receiveIndex:"+receiveIndex+" mid:"+mid);

						//视为被动关闭
						closeForIO(Close_Error);
					});

					return;
				}

				++_receiveMsgIndex;
			}

			if(_createResponseFunc!=null)
			{
				BaseResponse response=_createResponseFunc(mid);

				if(response==null)
				{
					if(ShineSetting.needMessageExistCheck)
					{
						ThreadControl.addMainFunc(()=>
						{
							Ctrl.throwError("未解析mid为" + mid + "的协议");
						});
					}

					return;
				}

				if(response.isNewOne)
				{
					_newResponseNum++;
				}

				response.socket=this;

				BaseResponse response2=response.readFromStream(_readStream);

				//变了
				if(response2!=response)
				{
					//直接回收 IO
					BytesControl.releaseResponse(response);
				}

				if(response2!=null)
				{
					if(ShineSetting.messageUsePool && response2.needRelease())
					{
						_responseCacheQueue.offer(response2);
					}

					//入队
					_receiveQueue.add(response2);
				}
			}
		}

		/** IO线程tick */
		public void socketTick()
		{
			if(!ShineSetting.messageUsePool)
				return;

			if(_responseCacheQueue.isEmpty())
				return;

			BaseResponse response;
			for(int i=_responseCacheQueue.Count-1;i>=0;--i)
			{
				response=_responseCacheQueue.peek();

				//执行过了
				if(response.executed)
				{
					_responseCacheQueue.poll();

					BytesControl.releaseResponse(response);
				}
				else
				{
					break;
				}
			}
		}

		//心跳回复(主线程)
		public void onRePing(int index)
		{
			refreshPingTime();

			_pingSending=false;

			//相同
			if(_pingIndex==index)
			{
				_ping=(int)(Ctrl.getTimer()-_sendPingTime);
				// Ctrl.print("看ping",_ping);
			}
			else
			{
				_ping=-1;
			}

			sendAbs(AckPingRequest.create(index));
		}

		/** 返回a1-a2 */
		public static short indexD(short a1,short a2)
		{
			int re=a1 - a2;

			if(re>short.MaxValue)
				re-=65536;

			if(re<short.MinValue)
				re+=65536;

			return (short)re;
		}

		/** 收到重连成功(IO消息) */
		public void onReconnectSuccess(int lastReceiveIndex)
		{
			//已不在重连中
			if(!_reconnecting)
			{
				closeForIO(Close_ReconnectFailed,false);
				return;
			}

			doReconnectNext((short)lastReceiveIndex,false);
		}

		/** ip地址信息组 */
		private class IPAddressInfo
		{
			/** ip地址信息 */
			public string host;

			private IPAddress[] _arr;

			private int _index=0;

			/** 6地址组 */
			// public IPAddressOneInfo ipv6=new IPAddressOneInfo();
			// public IPAddressOneInfo ipv4=new IPAddressOneInfo();
			//
			private IPAddress _select;
			//
			// /** 上次选择的是否是6 */
			// private bool _lastIs6=false;

			public void parse(IPAddress[] addresses)
			{
				_arr=addresses;
				_index=0;

				// int num4=0;
				// int num6=0;
				//
				// foreach(IPAddress v in addresses)
				// {
				// 	if(v.AddressFamily==AddressFamily.InterNetworkV6)
				// 	{
				// 		num6++;
				// 	}
				// 	else
				// 	{
				// 		num4++;
				// 	}
				// }
				//
				// IPAddress[] ipv4Arr=new IPAddress[num4];
				// IPAddress[] ipv6Arr=new IPAddress[num6];
				// num4=0;
				// num6=0;
				//
				// foreach(IPAddress v in addresses)
				// {
				// 	if(v.AddressFamily==AddressFamily.InterNetworkV6)
				// 	{
				// 		ipv6Arr[num6++]=v;
				// 	}
				// 	else
				// 	{
				// 		ipv4Arr[num4++]=v;
				// 	}
				// }
				//
				// ipv6.ipArr=ipv6Arr;
				// ipv4.ipArr=ipv4Arr;
			}

			/** 取一次地址(首次优选6，从6组中随机，如失败切4，再失败再切6，index每次取下一个地址，
			 * 如成功则每次均使用成功的地址，超时不计入失败)
			 */
			public IPAddress getOnce()
			{
				if(_select==null)
				{
					// IPAddressOneInfo one;
					//
					// if(_lastIs6)
					// {
					// 	if(!ipv4.isEmpty())
					// 	{
					// 		one=ipv4;
					// 		_lastIs6=false;
					// 	}
					// 	else
					// 	{
					// 		one=ipv6;
					// 	}
					// }
					// else
					// {
					// 	if(!ipv6.isEmpty())
					// 	{
					// 		one=ipv6;
					// 		_lastIs6=true;
					// 	}
					// 	else
					// 	{
					// 		one=ipv4;
					// 	}
					// }
					//
					// _select=one.getOne();

					int index=_index++;

					if(_index==_arr.Length)
						_index=0;

					_select=_arr[index];
				}

				return _select;
			}

			/** 失败一次 */
			public void failOnce()
			{
				_select=null;
			}

			/** 是否转了一圈 */
			public bool isCircle()
			{
				return _index==0;
			}
		}

		private class IPAddressOneInfo
		{
			/** 6地址组 */
			public IPAddress[] ipArr;
			private int _lastIndex=-1;

			public bool isEmpty()
			{
				return ipArr.Length==0;
			}

			public IPAddress getOne()
			{
				if(_lastIndex==-1)
				{
					_lastIndex=MathUtils.randomInt(ipArr.Length);
					return ipArr[_lastIndex];
				}
				else
				{
					return ipArr[(++_lastIndex)%ipArr.Length];
				}
			}
		}
	}
}