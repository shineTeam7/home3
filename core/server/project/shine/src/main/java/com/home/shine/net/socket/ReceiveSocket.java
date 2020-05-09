package com.home.shine.net.socket;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.request.PingRequest;
import com.home.shine.net.request.RePingRequest;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/** 接收连接 */
public class ReceiveSocket extends BaseSocket
{
	private ServerSocket _serverSocket;
	
	public ReceiveSocket()
	{
	
	}
	
	/** 初始化接收(netty线程) */
	public BaseSocketContent initReceive(Channel channel,ServerSocket serverSocket)
	{
		createNewContent();
		
		_serverSocket=serverSocket;
		_content.setChannel(channel);
		setConnected();
		
		return _content;
	}
	
	/** 初始化udp */
	public void initUDP(InetSocketAddress address,ServerSocket server)
	{
		//_udpAddress=address;
		//_server=server;
		//_udpChannel=_server.getUDPChannel();
		//
		////与address绑定
		//ioIndex=address.hashCode() & ThreadControl.ioThreadNumMark;
		//
		////标记连接
		//setConnected();
	}
	
	@Override
	protected void onClose()
	{
		_serverSocket.onReceiveSocketClose(this);
	}

	@Override
	protected void onSocketData(byte[] bb)
	{
		_serverSocket.onReceiveSocketData(this,bb);
	}
	
	/** 接收到重连消息 */
	public void onReceiveReconnect()
	{
	
	}
	
	/** 收到ping消息(IO线程) */
	public void onPing(int index)
	{
		refreshPingTime();
		
		_sendPingTime=Ctrl.getTimer();
		_pingIndex=index;
		sendAbsForIO(RePingRequest.create(index));
	}
	
	/** 收到ackPing消息 */
	public void onAckPing(int index)
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
	}
}
