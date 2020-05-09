package com.home.shine.net.socket;

import com.home.shine.ShineSetup;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.dataEx.AffairTimeOut;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.request.AckPingRequest;
import com.home.shine.net.request.PingRequest;
import com.home.shine.net.request.SocketReconnectRequest;
import com.home.shine.utils.StringUtils;

/** 发送连接 */
public abstract class SendSocket extends BaseSocket
{
	private static int _connectTimeMax=5;
	
	/** 发送连接标示ID */
	public int sendID=-1;
	
	/** 是否成功连接过(逻辑判定) */
	public boolean hasConnectSuccessed=false;
	
	/** 接收连接ID */
	private int _receiveID=-1;
	/** 接收令牌 */
	private int _receiveToken=-1;
	
	/** 是否无限尝试连接 */
	private boolean _isTryConnect=false;
	
	protected String _host;
	
	protected int _port;
	
	/** 是否需要ping包 */
	private boolean _needPing=true;
	/** 发送ping包时间序号 */
	private int _sendPingTimeCount=0;
	
	/** 连接计时 */
	private int _connectTime=0;
	
	/** 两次连接尝试的间隔(5秒) */
	private AffairTimeOut _tryConnectTime=new AffairTimeOut(this::retryConnect,5);
	
	/** 间隔(3秒) */
	private AffairTimeOut _reConnectTime=new AffairTimeOut(this::reconnectTimeOut,3);
	
	//netty
	
	public SendSocket()
	{
		//主动发送的不断
		setNeedPingCut(false);
	}
	
	@Override
	protected BaseSocketContent toCreateNewContent()
	{
		return new SendSocketContent(this);
	}
	
	@Override
	public void setIsClient(boolean value)
	{
		super.setIsClient(value);
		
		//_tryConnectTime.setTimeMax(value ? 3 : 1);
	}
	
	/** 连接单次(io线程) */
	private void toConnectOnce()
	{
		createNewContent();
		
		_state=Connecting;
		_connectTime=0;
		
		((SendSocketContent)_content).connect(_host,_port);
	}
	
	/** 设置接收连接信息 */
	public void setReceiveInfo(int receiveID,int token)
	{
		//已有
		if(_receiveID>0)
			return;
		
		_receiveID=receiveID;
		_receiveToken=token;
	}
	
	@Override
	protected void clear()
	{
		super.clear();
		
		_receiveID=-1;
		_receiveToken=-1;
	}
	
	//action
	
	/** 连接 */
	public void connect(String host,int port)
	{
		if(StringUtils.isNullOrEmpty(host) || port<=0)
		{
			Ctrl.errorLog("连接的地址缺失,host:",host,"port:",port);
		}
		
		_doIndex.incrementAndGet();
		
		ThreadControl.addIOFunc(ioIndex,()->
		{
			closeForIO(Close_Initiative);
			
			_host=host;
			_port=port;
			_isTryConnect=false;
			
			toConnectOnce();
		});
	}
	
	/** 尝试连接(不断连接，直到连接上为止) */
	public void tryConnect(String host,int port)
	{
		if(StringUtils.isNullOrEmpty(host) || port<=0)
		{
			Ctrl.errorLog("连接的地址缺失,host:",host,"port:",port);
		}
		
		_doIndex.incrementAndGet();
		
		ThreadControl.addIOFunc(ioIndex,()->
		{
			closeForIO(Close_Initiative);
			
			_host=host;
			_port=port;
			
			_isTryConnect=true;
			
			toConnectOnce();
		});
	}
	
	/** 再次连接,无限尝试 */
	public void reTryConnect()
	{
		ThreadControl.addIOFunc(ioIndex,()->
		{
			closeForIO(Close_Initiative);
			
			_isTryConnect=true;
			
			toConnectOnce();
		});
	}
	
	@Override
	protected void realClose()
	{
		_receiveID=-1;
		_receiveToken=-1;
		_reConnectTime.stop();
		
		super.realClose();
	}
	
	protected boolean canReconnect()
	{
		return _receiveID>0 && super.canReconnect();
	}
	
	@Override
	protected void onDisconnect()
	{
		super.onDisconnect();
		
		_reConnectTime.stop();
	}
	
	/** 停止连接 */
	protected void stopConnect()
	{
		if(_content!=null)
		{
			_content.close();
			_content=null;
		}
		
		_tryConnectTime.stop();
		
		if(_isTryConnect)
		{
			removeFromIO();
		}
	}
	
	/** 连接成功(io线程) */
	public void preConnectSuccessForIO()
	{
		ioInit();
		setConnected();
		
		//发一次ping
		sendPingRequest();
		
		//重连中
		if(_reconnecting && _receiveID>0)
		{
			refreshPingTime();
			sendAbsForIO(SocketReconnectRequest.create(_receiveID,_receiveToken,_receiveMsgIndex));
			_reConnectTime.start();
		}
		else
		{
			clear();
			onConnectSuccess();
		}
	}
	
	/** 连接成功(io线程) */
	protected abstract void onConnectSuccess();
	
	/** 连接失败(io线程) */
	public void preConnectFailedForIO()
	{
		if(!isConnecting())
			return;
		
		toCloseForConnect();
		
		if(_isTryConnect)
		{
			addToIO();
			_tryConnectTime.start();
		}
		else
		{
			removeFromIO();
			
			try
			{
				onConnectFailed();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
	
	/** try到时间(io线程) */
	private void retryConnect()
	{
		toCloseForConnect();
		toConnectOnce();
	}
	
	/** try到时间(io线程) */
	private void reconnectTimeOut()
	{
		reconnectFailed();
	}
	
	/** 连接失败(io线程) */
	public abstract void onConnectFailed();
	
	//ping
	
	@Override
	protected void onSecond()
	{
		super.onSecond();
		
		if(isConnect())
		{
			if(_needPing)
			{
				if((++_sendPingTimeCount) >= ShineSetting.pingTime)
				{
					_sendPingTimeCount=0;
					
					sendPingRequest();
				}
			}
		}
		else if(isConnecting())
		{
			//连接超时
			if((++_connectTime)>=_connectTimeMax)
			{
				Ctrl.log("连接中超时",_host, _port);
				
				preConnectFailedForIO();
			}
		}
		else
		{
			if(_isTryConnect)
			{
				_tryConnectTime.onSecond();
			}
		}

		
		if(_reconnecting)
		{
			_reConnectTime.onSecond();
		}
	}
	
	@Override
	protected void beginReconnect()
	{
		if(!ShineSetup.isExiting())
		{
			//无限尝试尝试
			_isTryConnect=true;
			toConnectOnce();
		}
	}
	
	/** 发送心跳(IO线程) */
	protected void sendPingRequest()
	{
		_sendPingTime=Ctrl.getTimer();
		sendAbsForIO(PingRequest.create(++_pingIndex));
	}
	
	/** 收到ping回复 */
	public void onRePing(int index)
	{
		refreshPingTime();
		
		//相同
		if(_pingIndex==index)
		{
			_ping=(int)(Ctrl.getTimer()-_sendPingTime);
		}
		else
		{
			_ping=-1;
		}
		
		//回复
		sendAbsForIO(AckPingRequest.create(index));
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
}
