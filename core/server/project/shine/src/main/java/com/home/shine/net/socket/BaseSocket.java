package com.home.shine.net.socket;

import com.home.shine.ShineSetup;
import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.NetWatchType;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.request.SocketReconnectFailedRequest;
import com.home.shine.net.request.SocketReconnectSuccessRequest;
import com.home.shine.support.pool.StringBuilderPool;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/** 连接基类(缓存了IP,不好做池) */
public abstract class BaseSocket
{
	/** 关闭的 */
	public static final int Closed=1;
	/** 连接中 */
	public static final int Connecting=2;
	/** 已连接 */
	public static final int Connected=3;
	
	//closeWay
	
	/** 主动关闭 */
	public static final int Close_Initiative=1;
	/** 被动-网络断开 */
	public static final int Close_ChannelInactive=2;
	/** 被动-重连失败 */
	public static final int Close_ReconnectFailed=3;
	/** 被动-解析出错 */
	public static final int Close_Error=4;
	/** 被动-超时 */
	public static final int Close_TimeOut=5;
	/** 被动-闪断测试 */
	public static final int Close_Test=6;
	
	//base
	/** 连接ID(没有ID时，为-1) */
	public int id=-1;
	/** io序号 */
	public int ioIndex;
	/** 连接类型 */
	public int type;
	/** 连接名字 */
	private String _name="";
	
	/** 令牌(接收连接的) */
	private int _token=-1;
	
	/** 当前状态(可认为是io线程写，其他线程读) */
	protected volatile int _state=Closed;
	/** 执行索引 */
	protected AtomicInteger _doIndex=new AtomicInteger();
	
	protected boolean _closeByInitiative;
	
	//udp
	
	//tcp
	
	protected volatile BaseSocketContent _content;
	//
	
	/** 是否为客户端使用 */
	protected boolean _isClient=false;
	/** 是否为webSocket */
	protected boolean _isWebSocket=false;
	
	//bytes
	
	private BytesReadStream _readStream=BytesReadStream.create();
	/** 写缓存流(io线程操作)(池化) */
	protected BytesWriteStream _writeStream;
	
	//limit
	protected int _bufSize=ShineSetting.serverMsgBufSize;
	
	protected boolean _needBufGrow=false;
	
	//reconnect
	/** 是否允许断线重连(默认关) */
	private boolean _openReconnect=false;
	/** 是否断线重连中 */
	protected volatile boolean _reconnecting=false;
	/** 重连剩余时间(秒) */
	private int _reconnectLastTime=0;
	
	//ip
	private String _remoteIP="";
	
	private int _remotePort=-1;
	
	//ping
	
	/** 是否需要ping包计算掉线 */
	private boolean _needPingCut=true;
	
	/** 心跳时间经过(秒) */
	private int _pingTimePass=0;
	/** 心跳计时(秒) */
	private int _pingKeepTime;
	
	/** ping序号 */
	protected int _pingIndex=0;
	/** 发送ping时间 */
	protected long _sendPingTime=0;
	/** ping延迟(ms) */
	protected int _ping=-1;
	
	/** 计时索引秒 */
	private int _timeIndex=0;
	
	//check
	
	/** 是否开启序号检测 */
	private boolean _openIndexCheck=false;
	
	/** 发送协议序号 */
	private short _sendMsgIndex=0;
	/** 接收协议序号 */
	protected short _receiveMsgIndex=0;
	
	/** 发送消息缓存队列 */
	private BaseRequest[] _sendCacheRequestQueue;
	/** 断线时发送的序号 */
	private short _disConnectLastSendIndex=0;
	
	public BaseSocket()
	{
		ioIndex=this.hashCode() & ThreadControl.ioThreadNumMark;
	}
	
	public int getBufSize()
	{
		return _bufSize;
	}
	
	/** 获取状态 */
	public int getState()
	{
		return _state;
	}
	
	public void setType(int value)
	{
		type=value;
		_name=SocketType.getName(type);
	}
	
	/** 是否为客户端 */
	public void setIsClient(boolean value)
	{
		_isClient=value;
		
		_pingKeepTime=_isClient ? ShineSetting.pingKeepTimeClient : ShineSetting.pingKeepTimeServer;
		_bufSize=_isClient ? ShineSetting.clientMsgBufSize : ShineSetting.serverMsgBufSize;
		_needBufGrow=!_isClient;
		
		if(_isClient && ShineSetting.needClientBufGrow)
			_needBufGrow=true;
		
		//客户端的开检测
		setOpenIndexCheck(_isClient);
	}
	
	/** 是否为客户端 */
	public boolean isClient()
	{
		return _isClient;
	}
	
	public void setIsWebSocket(boolean value)
	{
		_isWebSocket=value;
	}
	
	/** 是否为webSocket */
	public boolean isWebSocket()
	{
		return _isWebSocket;
	}
	
	/** 设置连接成功 */
	protected void setConnected()
	{
		_state=Connected;
	}
	
	/** 是否连接着对逻辑而言 */
	public boolean isConnectForLogic()
	{
		return _reconnecting || _state==Connected;
	}
	
	/** 是否连接着 */
	public boolean isConnect()
	{
		return _state==Connected;
	}
	
	/** 是否正在连接中 */
	public boolean isConnecting()
	{
		return _state==Connecting;
	}
	
	/** 是否断线重连中 */
	public boolean isReconnecting()
	{
		return _reconnecting;
	}
	
	public int getDoIndex()
	{
		return _doIndex.get();
	}
	
	/** 设置令牌 */
	public void setToken(int value)
	{
		_token=value;
	}
	
	/** 获取令牌 */
	public int getToken()
	{
		return _token;
	}
	
	/** 获取网络延迟(一来一回,包括IO线程消耗，不包括切主线程) */
	public int getPing()
	{
		return _ping;
	}
	
	/** 创建新内容 */
	public void createNewContent()
	{
		int index=_doIndex.incrementAndGet();
		_content=toCreateNewContent();
		_content.setDoIndex(index);
	}
	
	/** 创建content类 */
	protected BaseSocketContent toCreateNewContent()
	{
		return new BaseSocketContent(this);
	}
	
	/** 取出读流(temp存) */
	public BytesReadStream getReadStream()
	{
		return _readStream;
	}
	
	protected void addToIO()
	{
		//先添加
		ThreadControl.getIOThread(ioIndex).addSocket(this);
	}
	
	protected void removeFromIO()
	{
		//先添加
		ThreadControl.getIOThread(ioIndex).removeSocket(this);
	}
	
	/** io初始化(io线程) */
	public void ioInit()
	{
		addToIO();
		
		if(_writeStream==null)
		{
			_writeStream=ThreadControl.getIOThread(ioIndex).bytesWriteStreamPool.getOne();
		}
	}
	
	/** io析构(io线程) */
	public void ioDispose()
	{
		removeFromIO();
		
		if(_writeStream!=null)
		{
			ThreadControl.getIOThread(ioIndex).bytesWriteStreamPool.back(_writeStream);
			_writeStream=null;
		}
	}
	
	/** 每帧调用(io线程) */
	public void onFrame()
	{
		_timeIndex+=ShineSetting.ioThreadFrameDelay;
		
		if(_timeIndex >= 1000)
		{
			_timeIndex=0;
			
			onSecond();
		}
		
		if(isConnect())
		{
			toFlush();
		}
	}
	
	/** 收到数据(IO线程) */
	public void onePiece(byte[] bytes)
	{
		//更新时间
		refreshPingTime();
		onSocketData(bytes);
	}
	
	/** 执行socket数据(io线程) */
	abstract protected void onSocketData(byte[] bb);
	
	/** 推送一次(io线程) */
	protected void toFlush()
	{
		if(_writeStream!=null && _writeStream.length()>0)
		{
			toSendStream(_writeStream);
			
			_writeStream.clear();
		}
	}
	
	/** 推字节(IO线程) */
	private void toSendStream(BytesWriteStream stream)
	{
		BaseSocketContent content;
		if((content=_content)==null)
			return;
		
		if(ShineSetting.needNetFlowWatch)
		{
			WatchControl.netFlows[_isClient ? NetWatchType.ClientSend : NetWatchType.ServerSend].addAndGet(stream.length());
			WatchControl.netFlowsWithHead[_isClient ? NetWatchType.ClientSend : NetWatchType.ServerSend].addAndGet(stream.length()+ShineSetting.netTCPPacketHeadLength);
		}
		
		content.doSendStream(stream);
	}
	
	/** 每秒(不准) */
	protected void onSecond()
	{
		if(_reconnecting)
		{
			if(--_reconnectLastTime<=0)
			{
				_reconnectLastTime=0;
				
				Ctrl.log("重连超时,连接关闭,socketID:",this.id);
				
				reconnectFailed();
			}
		}
		
		if(isConnect())
		{
			if(_needPingCut && ShineSetting.needPingCut)
			{
				++_pingTimePass;
				
				if(_pingTimePass>_pingKeepTime)
				{
					Ctrl.log("连接超时,强制关闭,socketID:",this.id);
					preClosedForTimeOut();
				}
			}
		}
	}
	
	/** 刷ping的时间(IO线程) */
	public void refreshPingTime()
	{
		_pingTimePass=0;
	}
	
	protected void clear()
	{
		//先清空上次的
		if(_writeStream!=null)
			_writeStream.clear();
		
		_reconnecting=false;
		_reconnectLastTime=0;
		_sendMsgIndex=0;
		_receiveMsgIndex=0;
		_pingTimePass=0;
	}
	
	//close部分
	
	/** 关闭连接(主动)(主线程/逻辑线程)(会把未发出的消息都发出) */
	public void close()
	{
		_doIndex.incrementAndGet();
		
		ThreadControl.addIOFunc(ioIndex,()->
		{
			closeForIO(Close_Initiative);
		});
	}
	
	/** 闪断测试 */
	public void closeTest()
	{
		ThreadControl.addIOFunc(ioIndex,()->
		{
			closeForIO(Close_Test);
		});
	}
	
	/** 关闭(io线程) */
	public void closeForIO(int reason)
	{
		closeForIO(reason,true);
	}
	
	/** 关闭(io线程) */
	public void closeForIO(int reason,boolean canRec)
	{
		if(_state==Closed)
			return;
		
		//正在连接中
		if(isConnecting())
		{
			stopConnect();
		}
		
		_state=Closed;
		
		_doIndex.incrementAndGet();
		
		Ctrl.debugLog("连接断开,socketID:",this.id,"reason:",reason);
		
		//InetSocketAddress remoteAddress=getRemoteAddress();
		//
		//if(remoteAddress!=null)
		//{
		//	Ctrl.debugLog("远端ip:",remoteAddress.getHostString(),"port:",remoteAddress.getPort());
		//}
		//else
		//{
		//	Ctrl.debugLog("远端ip无");
		//}

		boolean isInitiative=reason==Close_Initiative;
		
		if(isInitiative)
		{
			//推掉剩余的
			toFlush();
		}
		
		onDisconnect();
		
		//重连中，会再次重连
		if(_openReconnect && !isInitiative && canRec && canReconnect()) //!_reconnecting &&
		{
			Ctrl.log("连接断线，进入重连阶段,socketID:",this.id);
			_reconnecting=true;
			_disConnectLastSendIndex=_sendMsgIndex;
			_reconnectLastTime=ShineSetting.socketReConnectKeepTime;
			
			_closeByInitiative=isInitiative;
			beginReconnect();
		}
		else
		{
			_closeByInitiative=isInitiative;
			realClose();
		}
	}
	
	public void reConnectFailedClose()
	{
		sendAbsForIO(SocketReconnectFailedRequest.create());
		toFlush();
		closeForIO(BaseSocket.Close_ReconnectFailed,false);
	}
	
	protected void toCloseForConnect()
	{
		if(_state==Closed)
			return;
		
		_state=Closed;
		
		_doIndex.incrementAndGet();
		
		onDisconnect();
	}
	
	/** 是否可重连 */
	protected boolean canReconnect()
	{
		return !ShineSetup.isExiting();
	}
	
	/** 停止连接 */
	protected void stopConnect()
	{
	
	}
	
	/** 收到重连失败(io线程) */
	public void onReconnectFailed()
	{
		reconnectFailed();
	}
	
	/** 重连失败(IO线程) */
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
	
	/** 实际关闭(IO线程) */
	protected void realClose()
	{
		_reconnecting=false;
		_disConnectLastSendIndex=0;
		
		//空
		if(_sendCacheRequestQueue!=null)
		{
			Arrays.fill(_sendCacheRequestQueue,null);
		}
		
		_sendMsgIndex=0;
		//_sendCheckIndex=0;
		_receiveMsgIndex=0;
		
		try
		{
			onClose();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		ioDispose();
	}
	
	/** 连接被关闭(io线程) */
	private void preClosedForTimeOut()
	{
		_pingTimePass=0;
		//ThreadControl.addOtherIOFunc();
		Ctrl.log("连接被断开:超时",this.id);
		closeForIO(Close_TimeOut);
	}
	
	/** 连接断开 */
	protected void onDisconnect()
	{
		if(_content!=null)
		{
			_content.close();
			_content=null;
		}
	}
	
	/** 连接被断开(io线程) */
	abstract protected void onClose();
	
	/** 上次关闭是否主动 */
	public boolean lastCloseByInitiative()
	{
		return _closeByInitiative;
	}
	
	/** 开始重连(io线程) */
	protected void beginReconnect()
	{
	
	}
	
	//address
	
	private InetSocketAddress getRemoteAddress()
	{
		BaseSocketContent content;
		
		if((content=_content)!=null)
			return content.getRemoteAddress();
		
		return null;
	}
	
	/** 获取连接目标的IP(用的是HostString) */
	public String remoteIP()
	{
		if(_remoteIP.isEmpty())
		{
			InetSocketAddress addr=getRemoteAddress();
			
			if(addr!=null)
			{
				_remoteIP=addr.getHostString();
			}
		}
		
		return _remoteIP;
	}
	
	/** 获取连接目标端口 */
	public int remotePort()
	{
		if(_remotePort==-1)
		{
			InetSocketAddress addr=getRemoteAddress();
			
			if(addr!=null)
			{
				_remotePort=addr.getPort();
			}
		}
		
		return _remotePort;
	}
	
	//check
	
	/** 设置是否ping包掉线 */
	public void setNeedPingCut(boolean bool)
	{
		_needPingCut=bool;
	}
	
	/** 是否开启断线重连 */
	public void setOpenReconnect(boolean bool)
	{
		_openReconnect=bool;
	}
	
	/** 是否开启断线重连 */
	public boolean isOpenReconnect()
	{
		return _openReconnect;
	}

	/** 设置buf如果到达上限，是否自动扩容 */
	public void setNeedBufGrow(boolean bool)
	{
		_needBufGrow=bool;
	}
	
	public boolean getNeedBufGrow()
	{
		return _needBufGrow;
	}
	
	/** 开启序号检测 */
	public void setOpenIndexCheck(boolean bool)
	{
		_openIndexCheck=bool;
		
		if(bool && _sendCacheRequestQueue==null)
		{
			_sendCacheRequestQueue=new BaseRequest[ShineSetting.requestCacheLen];
		}
	}
	
	/** 是否开启序号检测 */
	public boolean getOpenIndexCheck()
	{
		return _openIndexCheck;
	}
	
	/** 获取接收协议序号 */
	public int getReceiveMsgIndex()
	{
		return _receiveMsgIndex;
	}
	
	/** 设置接收消息序号 */
	public void addReceiveMsgIndex()
	{
		++_receiveMsgIndex;
	}
	
	//send
	
	/** 立即推送字节(cross用) */
	public void sendBytesAbs(byte[] bytes)
	{
		BaseSocketContent content;
		
		if((content=_content)==null)
			return;
		
		content.doSendByteArr(bytes);
	}
	
	/** 执行写入流(io线程) */
	protected void doWriteRequestToStream(BaseRequest request,int index)
	{
		BytesWriteStream writeStream;
		
		//写到stream里
		if((writeStream=_writeStream)!=null)
		{
			int pos=writeStream.getPosition();
			
			toWriteRequestToStream(writeStream,request,index);
			
			int len=writeStream.getPosition()-pos;
			
			if(ShineSetting.needThreadWatch)
			{
				//观测
				WatchControl.netThreadWatchs[ioIndex].datas[isClient() ? NetWatchType.ClientSend : NetWatchType.ServerSend].addOne(request.getDataID(),len);
			}
		}
	}
	
	/** 实际执行写request到stream */
	private void toWriteRequestToStream(BytesWriteStream stream,BaseRequest request,int index)
	{
		stream.clearBooleanPos();
		
		int limit=request.getMessageLimit();
		stream.setWriteLenLimit(limit);
		
		int startPos=stream.getPosition();
		
		int mid=request.getDataID();
		
		//写协议ID(原生写)
		stream.natureWriteUnsignedShort(mid);
		
		if(_openIndexCheck && !BytesControl.isIgnoreMessage(mid))
		{
			stream.natureWriteShort(index);
		}
		
		request.writeToStream(stream);
		
		int endPos=stream.getPosition();
		int len=endPos-startPos;
		
		if(len>=limit)
		{
			Ctrl.warnLog("request长度超出上限",len);
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
	}
	
	//reconnect
	
	//public
	
	/** 将目标连接的content移植到当前连接上(IO线程) */
	public void doReconnect(BaseSocket newSocket,short lastReceiveIndex)
	{
		//不再判定是否重连中,因为udp
		//if(!_reconnecting)
		//	return;
		
		if(!newSocket.isConnect())
			return;
		
		int index=_doIndex.incrementAndGet();
		
		BaseSocketContent oldContent=_content;
		
		if(oldContent!=null)
			oldContent.close();
		
		_content=newSocket._content;
		newSocket._content=null;
		
		Ctrl.log("socket重连交换:",id);
		
		_content.setDoIndex(index);
		_content.setParent(this);
		setConnected();
		
		newSocket.close();
		ioInit();
		
		doReconnectNext(lastReceiveIndex,true);
		
		//TODO:其他的提示
	}
	
	/** 重连的下一步(IO线程) */
	protected void doReconnectNext(short lastReceiveIndex,boolean needReMsg)
	{
		//清空
		_writeStream.clear();
		
		short dd=indexD(_sendMsgIndex,lastReceiveIndex);
		
		if(dd<0 || dd>=ShineSetting.requestCacheLen)
		{
			Ctrl.warnLog("重连时，消息缓存已失效");
			reConnectFailedClose();
			return;
		}
		
		if(needReMsg)
		{
			sendAbsForIO(SocketReconnectSuccessRequest.create(_receiveMsgIndex));
		}
		
		for(short i=lastReceiveIndex;i!=_sendMsgIndex;i++)
		{
			BaseRequest request=_sendCacheRequestQueue[i & ShineSetting.requestCacheMark];
			
			if(request==null)
			{
				Ctrl.errorLog("不该找不到消息");
				reConnectFailedClose();
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
		
		Ctrl.log("socket重连成功:",id);
	}
	
	/** 发abs消息(io线程) */
	public final void sendAbsForIO(BaseRequest request)
	{
		request.preSend();
		
		if(isConnect())
		{
			doWriteRequestToStream(request,0);
		}
	}
	
	/** 发送消息(逻辑线程调用)(支持多线程访问) */
	//关注点 发送消息
	public void send(BaseRequest request)
	{
		if(ShineSetting.needShowMessage)
		{
			int mid=request.getDataID();
			
			Ctrl.debugLog("发消息:",getInfo(),"mid:"+mid,ShineSetting.needShowMessageDetail ? request.toDataString() : request.getDataClassName());
		}
		
		//重连中
		if(checkRequestNeedSend(request))
		{
			request.preSend();
			
			ThreadControl.addIOFunc(ioIndex,()->
			{
				sendRequestForIO(request);
			});
		}
	}
	
	private boolean checkRequestNeedSend(BaseRequest request)
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
	
	/** 发送request(IO线程) */
	protected void sendRequestForIO(BaseRequest request)
	{
		if(!checkRequestNeedSend(request))
			return;
		
		boolean needIndex=false;
		short index=0;
		
		if(_openIndexCheck && !BytesControl.isIgnoreMessage(request.getDataID()))
		{
			needIndex=true;
			index=_sendMsgIndex++;
			//存起
			_sendCacheRequestQueue[index & ShineSetting.requestCacheMark]=request;
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
	
	/** 返回a1-a2 */
	public static short indexD(short a1,short a2)
	{
		int re=a1 - a2;
		
		if(re>Short.MAX_VALUE)
			re-=65536;
		
		if(re<Short.MIN_VALUE)
			re+=65536;
		
		return (short)re;
	}
	
	/** 获取连接信息 */
	public String getInfo()
	{
		StringBuilder sb=StringBuilderPool.create();
		writeInfo(sb);
		return StringBuilderPool.releaseStr(sb);
	}
	
	/** 写入连接信息 */
	public void writeInfo(StringBuilder sb)
	{
		sb.append("socketID:");
		sb.append(id);
		sb.append(" type:");
		sb.append(type);
		sb.append(" ");
		sb.append(_name);
	}
}
