package com.home.shine.net.socket;

import com.home.shine.constlist.NetWatchType;
import com.home.shine.constlist.SocketType;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.net.kcp.netty.IUkcpServerBootstrap;
import com.home.shine.net.kcp.netty.UkcpChannel;
import com.home.shine.net.kcp.netty.UkcpChannelOption;
import com.home.shine.net.kcp.netty.UkcpServerChannel;
import com.home.shine.net.request.SocketConnectSuccessRequest;
import com.home.shine.utils.MathUtils;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

import java.util.List;

/**
 * 长连
 */
public abstract class ServerSocket
{
	/**
	 * 端口
	 */
	protected int _port;
	
	/**
	 * 是否为客户端
	 */
	protected boolean _isClient=false;
	
	/**
	 * 是否需要协议解析(不解析就直接是传来的byte[])
	 */
	protected boolean _needAnalyze=true;
	
	//limit
	/**
	 * 收msg缓冲区大小
	 */
	protected int _bufSize=ShineSetting.serverMsgBufSize;
	
	//netty
	
	private ServerBootstrap _sb;
	
	protected Channel _ch;
	
	public ServerSocket(int port)
	{
		_port=port;
	}
	
	/**
	 * 是否为客户端端口(影响bufSize)
	 */
	public void setIsClient(boolean value)
	{
		_isClient=value;
		
		_bufSize=_isClient ? ShineSetting.clientMsgBufSize : ShineSetting.serverMsgBufSize;
	}
	
	/**
	 * 是否为客户端端口
	 */
	public boolean isClient()
	{
		return _isClient;
	}
	
	/**
	 * 是否需要协议解析
	 */
	public void setNeedAnalyze(boolean value)
	{
		_needAnalyze=value;
	}
	
	/**
	 * 是否需要协议解析
	 */
	public boolean getNeedAnalyze()
	{
		return _needAnalyze;
	}
	
	/**
	 * 启动
	 */
	public void start()
	{
		if(isClient() && ShineSetting.useKCP)
		{
			Ctrl.log("启动udp端口",_port);
			toStartKcp();
		}
		else
		{
			Ctrl.log("启动tcp端口",_port);
			toStartTCP();
		}
	}
	
	private void toStartTCP()
	{
		int sendBufSize=_isClient ? ShineSetting.clientSocketSendBuf : ShineSetting.serverSocketBuf;
		int receiveBufSize=_isClient ? ShineSetting.clientSocketReceiveBuf : ShineSetting.serverSocketBuf;
		
		ServerBootstrap sb=_sb=new ServerBootstrap();
		
		sb.group(NettyGroup.getBossGroup(),NettyGroup.getWorkerGroup());
		sb.channel(NettyGroup.EpollAvailable ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
		sb.childHandler(new NettyServerSocketInitializer());
		
		sb.option(ChannelOption.SO_BACKLOG,ShineSetting.nettyBackLog);
		
		sb.childOption(ChannelOption.SO_REUSEADDR,true);
		sb.childOption(ChannelOption.TCP_NODELAY,true);
		sb.childOption(ChannelOption.SO_KEEPALIVE,true);
		sb.childOption(ChannelOption.SO_SNDBUF,sendBufSize);
		sb.childOption(ChannelOption.SO_RCVBUF,receiveBufSize);
		sb.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,60 * 1000);
		
		try
		{
			_ch=sb.bind(_port).sync().channel();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			onStartFailed();
		}
	}
	
	private void toStartKcp()
	{
		int sendBufSize=_isClient ? ShineSetting.clientSocketSendBuf : ShineSetting.serverSocketBuf;
		int receiveBufSize=_isClient ? ShineSetting.clientSocketReceiveBuf : ShineSetting.serverSocketBuf;
		
		try
		{
			
			Class<?> cls=Class.forName(ShineGlobal.kcpClassQName);
			
			IUkcpServerBootstrap b=(IUkcpServerBootstrap)cls.newInstance();
			AbstractBootstrap ab=(AbstractBootstrap)b;
			
			ab.group(NettyGroup.getUdpWorkerGroup()).channel(UkcpServerChannel.class);
			b.childHandler(new ChannelInitializer<UkcpChannel>()
			{
				@Override
				protected void initChannel(UkcpChannel ukcpChannel) throws Exception
				{
					ChannelPipeline pipeline=ukcpChannel.pipeline();
					
					preInitChannel(pipeline);
					
				}
			});
			
			b.nodelay(true,16,2,true);
			
			b.childOption(UkcpChannelOption.UKCP_RCV_WND,ShineSetting.kcpWndSize);
			b.childOption(UkcpChannelOption.UKCP_SND_WND,ShineSetting.kcpWndSize);
			b.childOption(UkcpChannelOption.UKCP_MTU,ShineSetting.kcpMTU);
			
			b.childOption(UkcpChannelOption.SO_SNDBUF,sendBufSize);
			b.childOption(UkcpChannelOption.SO_RCVBUF,receiveBufSize);
			_ch=b.bind(_port).sync().channel();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			onStartFailed();
		}
	}
	
	/**
	 * 关闭端口
	 */
	public ChannelFuture close()
	{
		if(_ch!=null)
		{
			_ch.close();
			_ch=null;
		}
		
		_sb=null;
		
		return null;
	}
	
	/**
	 * 启动失败
	 */
	public void onStartFailed()
	{
		Ctrl.warnLog("Socket端口启动失败:",_port,"请检查是否起了多个进程。");
	}
	
	/**
	 * pre新连接(netty线程)
	 */
	public void preNewReceiveSocket(ReceiveSocket socket)
	{
		//注册socket,为onFrame
		ThreadControl.addOtherIOFunc(socket.ioIndex,()->
		{
			newReceiveSocketForIO(socket);
		});
	}
	
	private void newReceiveSocketForIO(ReceiveSocket socket)
	{
		socket.ioInit();
		onNewReceiveSocket(socket);
		//设置令牌
		socket.setToken(MathUtils.getToken());
		//发送回执信息
		socket.sendAbsForIO(SocketConnectSuccessRequest.create(socket.id,socket.getToken()));
	}
	
	/**
	 * 初始化接收连接
	 */
	protected void initReceiveSocket(ReceiveSocket socket)
	{
		socket.setIsClient(_isClient);
		socket.setType(_isClient ? SocketType.ClientReceive : SocketType.ServerReceive);
	}
	
	/**
	 * 创建接收链接
	 */
	public ReceiveSocket createReceiveSocket()
	{
		return new ReceiveSocket();
	}
	
	/**
	 * 新连接(给予id)(io线程)
	 */
	public abstract void onNewReceiveSocket(ReceiveSocket socket);
	
	/**
	 * 连接关闭处理(主动+被动)(io线程)
	 */
	public abstract void onReceiveSocketClose(ReceiveSocket socket);
	
	/**
	 * 接收连接收到消息
	 */
	public abstract void onReceiveSocketData(ReceiveSocket socket,byte[] data);
	
	//nettyMethod
	
	protected void preInitChannel(ChannelPipeline pipeline)
	{
		if(_needAnalyze)
		{
			toInitChannel(pipeline);
		}
		else
		{
			pipeline.addLast("decoder",new ByteArrayDecoder());
			pipeline.addLast("encoder",new ByteArrayEncoder());
			pipeline.addLast("handler",new NettySocketServerHandler(this));
		}
	}
	
	protected void toInitChannel(ChannelPipeline pipeline)
	{
		pipeline.addLast("decoder",new CustomHandlerDecoder(this));
		pipeline.addLast("encoder",new ByteBufEncoder());
	}
	
	//inner class
	
	private class NettyServerSocketInitializer extends ChannelInitializer<SocketChannel>
	{
		@Override
		protected void initChannel(SocketChannel paramC) throws Exception
		{
			ChannelPipeline pipeline=paramC.pipeline();
			
			preInitChannel(pipeline);
		}
	}
	
	/**
	 * 自定义协议解析
	 */
	private class CustomHandlerDecoder extends MessageToMessageDecoder<ByteBuf>
	{
		private ServerSocket _server;
		
		private BaseSocketContent _content;
		
		public CustomHandlerDecoder(ServerSocket server)
		{
			_server=server;
			
		}
		
		@Override
		protected void decode(ChannelHandlerContext ctx,ByteBuf msg,List<Object> out) throws Exception
		{
			if(ShineSetting.needNetFlowWatch)
			{
				WatchControl.netFlows[_server.isClient() ? NetWatchType.ClientReceive : NetWatchType.ServerReceive].addAndGet(msg.readableBytes());
				WatchControl.netFlowsWithHead[_server.isClient() ? NetWatchType.ClientReceive : NetWatchType.ServerReceive].addAndGet(msg.readableBytes() + ShineSetting.netTCPPacketHeadLength);
			}
			
			_content.onBuffer(msg);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)
		{
			//Ctrl.print(cause.getMessage());
			// cause.printStackTrace();
			// ctx.close();
		}
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception
		{
			ChannelConfig config=ctx.channel().config();
			
			DefaultSocketChannelConfig socketConfig=(DefaultSocketChannelConfig)config;
			
			socketConfig.setPerformancePreferences(0,1,2);
			socketConfig.setAllocator(PooledByteBufAllocator.DEFAULT);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);
			
			ReceiveSocket socket=createReceiveSocket();
			initReceiveSocket(socket);
			_content=socket.initReceive(ctx.channel(),_server);
			_server.preNewReceiveSocket(socket);
		}
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx)
		{
			try
			{
				super.channelInactive(ctx);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			_content.preClosed();
		}
		
		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
		{
			try
			{
				super.channelUnregistered(ctx);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
	}
	
	/**
	 * 不解析的handler
	 */
	private class NettySocketServerHandler extends SimpleChannelInboundHandler<Object>
	{
		private ServerSocket _server;
		
		private BaseSocketContent _content;
		
		public NettySocketServerHandler(ServerSocket server)
		{
			_server=server;
		}
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception
		{
			ChannelConfig config=ctx.channel().config();
			
			DefaultSocketChannelConfig socketConfig=(DefaultSocketChannelConfig)config;
			
			socketConfig.setPerformancePreferences(0,1,2);
			
			socketConfig.setAllocator(PooledByteBufAllocator.DEFAULT);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);
			
			ReceiveSocket socket=createReceiveSocket();
			initReceiveSocket(socket);
			_content=socket.initReceive(ctx.channel(),_server);
			_server.preNewReceiveSocket(socket);
		}
		
		@Override
		public void channelInactive(ChannelHandlerContext ctx)
		{
			try
			{
				super.channelInactive(ctx);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
			
			_content.preClosed();
		}
		
		@Override
		public void channelUnregistered(ChannelHandlerContext ctx)
		{
			try
			{
				super.channelUnregistered(ctx);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		@Override
		protected void channelRead0(ChannelHandlerContext ctx,Object arg) throws Exception
		{
			byte[] bb=(byte[])arg;
			
			if(ShineSetting.needNetFlowWatch)
			{
				WatchControl.netFlows[_server.isClient() ? NetWatchType.ClientReceive : NetWatchType.ServerReceive].addAndGet(bb.length);
				WatchControl.netFlowsWithHead[_server.isClient() ? NetWatchType.ClientReceive : NetWatchType.ServerReceive].addAndGet(bb.length + ShineSetting.netTCPPacketHeadLength);
			}
			
			_content.preSocketData(bb);
		}
	}
}
