package com.home.shine.net;

import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.kcp.netty.UkcpChannelOption;
import com.home.shine.net.kcp.netty.UkcpClientChannel;
import com.home.shine.net.ssl.MTrustAllManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;

/** netty共享部分 */
public class NettyGroup
{
	public static boolean EpollAvailable=false;
	
	private static EventLoopGroup _bossGroup;
	private static EventLoopGroup _workerGroup;
	
	private static EventLoopGroup _udpWorkerGroup;
	
	//private static Bootstrap _clientSendSocketBoot;
	//private static Bootstrap _serverSendSocketBoot;
	
	
	private static boolean _sslInited=false;
	
	//private static SslContext _clientSslCtx;
	private static SslContext _serverSslCtx;
	
	private static SSLContext _clientCX;
	private static SSLContext _serverCX;
	
	//TODO:https没写完
	
	/** 初始化 */
	public static void init()
	{
		EpollAvailable=Epoll.isAvailable();
		
		if(EpollAvailable)
		{
			_bossGroup=new EpollEventLoopGroup(1);
			_workerGroup=new EpollEventLoopGroup(ShineSetting.nettyWorkGroupThreadNum);
		}
		else
		{
			_bossGroup=new NioEventLoopGroup(1);
			_udpWorkerGroup=_workerGroup=new NioEventLoopGroup(ShineSetting.nettyWorkGroupThreadNum);
		}
		
		initSSL();
	}
	
	/** 关闭(主线程) */
	public static void exit()
	{
		_bossGroup.shutdownGracefully();
		_workerGroup.shutdownGracefully();
	}
	
	public static EventLoopGroup getBossGroup()
	{
		return _bossGroup;
	}
	
	public static EventLoopGroup getWorkerGroup()
	{
		return _workerGroup;
	}
	
	public static synchronized EventLoopGroup getUdpWorkerGroup()
	{
		if(_udpWorkerGroup==null)
			_udpWorkerGroup=new NioEventLoopGroup(ShineSetting.nettyWorkGroupThreadNum);
		return _udpWorkerGroup;
	}
	
	private static Bootstrap createSocketBoot(boolean isClient)
	{
		Bootstrap bs=new Bootstrap();
		
		int sendBufSize=isClient ? ShineSetting.clientSocketSendBuf : ShineSetting.serverSocketBuf;
		int receiveBufSize=isClient ? ShineSetting.clientSocketReceiveBuf : ShineSetting.serverSocketBuf;
		
		if(isClient && ShineSetting.useKCP)
		{
			bs.group(getUdpWorkerGroup());
			
			bs.channel(UkcpClientChannel.class);
			
			bs.option(UkcpChannelOption.UKCP_NODELAY, true);
			bs.option(UkcpChannelOption.UKCP_INTERVAL, 16);
			bs.option(UkcpChannelOption.UKCP_FAST_RESEND, 2);
			bs.option(UkcpChannelOption.UKCP_NOCWND, true);
			
			bs.option(UkcpChannelOption.UKCP_RCV_WND, ShineSetting.kcpWndSize);
			bs.option(UkcpChannelOption.UKCP_SND_WND, ShineSetting.kcpWndSize);
			bs.option(UkcpChannelOption.UKCP_MTU, ShineSetting.kcpMTU);
			
			bs.option(UkcpChannelOption.SO_SNDBUF, sendBufSize);
			bs.option(UkcpChannelOption.SO_RCVBUF, receiveBufSize);
		}
		else
		{
			bs.group(getWorkerGroup());
			
			bs.channel(NettyGroup.EpollAvailable ? EpollSocketChannel.class : NioSocketChannel.class);
			
			bs.option(ChannelOption.SO_REUSEADDR,true);
			bs.option(ChannelOption.TCP_NODELAY,true);
			bs.option(ChannelOption.SO_KEEPALIVE,true);
			
			bs.option(ChannelOption.SO_SNDBUF,sendBufSize);
			bs.option(ChannelOption.SO_RCVBUF,receiveBufSize);
		}
		
		return bs;
	}
	
	public static Bootstrap createHttpBoot(boolean isClient)
	{
		Bootstrap bs=new Bootstrap();
		bs.group(_workerGroup);
		bs.channel(NettyGroup.EpollAvailable ? EpollSocketChannel.class : NioSocketChannel.class);
		
		bs.option(ChannelOption.SO_REUSEADDR,true);
		bs.option(ChannelOption.TCP_NODELAY,true);
		bs.option(ChannelOption.SO_KEEPALIVE,true);
		
		int sendBufSize=isClient ? ShineSetting.clientHttpSendBuf : ShineSetting.serverHttpSendBuf;
		int receiveBufSize=isClient ? ShineSetting.clientHttpReceiveBuf : ShineSetting.serverHttpReceiveBuf;
		bs.option(ChannelOption.SO_SNDBUF,sendBufSize);
		bs.option(ChannelOption.SO_RCVBUF,receiveBufSize);
		
		bs.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,60 * 1000);
		
		return bs;
	}
	
	/** 创建发送socket(异步) */
	public static Channel createSendSocket(String host,int port,boolean isClient,ChannelInitializer<Channel> initializer,ChannelFutureListener callback)
	{
		Bootstrap bs=createSocketBoot(isClient);
		
		bs.handler(initializer);
		
		return bs.connect(host,port).addListener(callback).channel();
	}
	
	/** 创建发送http(异步) */
	public static Channel createSendHttp(String host,int port,boolean isClient,ChannelInitializer<Channel> initializer,ChannelFutureListener callback)
	{
		Bootstrap bs=createHttpBoot(isClient);
		
		bs.handler(initializer);
		
		Channel channel=bs.connect(host,port).addListener(callback).channel();
		
		return channel;
	}
	
	public static void initSSL()
	{
		if(_sslInited)
			return;
		
		_sslInited=true;
		
		//客户端按全信任走
		try
		{
			MTrustAllManager mTrust = new MTrustAllManager();
			
			// 信任所有
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, new TrustManager[]{mTrust}, null);
			_clientCX=sc;
			
		}
		catch(Exception e)
		{
		
		}
		
		//客户端需要SSL
		if(ShineSetting.clientHttpUseHttps || ShineSetting.clientWebSocketUseWSS)
		{
			if(ShineSetting.sslUseSelfSigned)
			{
				//try
				//{
				//	_clientSslCtx=SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
				//
				//}
				//catch(SSLException e)
				//{
				//	e.printStackTrace();
				//}
				
				try
				{
					SelfSignedCertificate ssc=new SelfSignedCertificate();
					_serverSslCtx=SslContextBuilder.forServer(ssc.certificate(),ssc.privateKey()).build();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				
				//try
				//{
				//	String password="mypassword";
				//
				//	KeyStore ks=KeyStore.getInstance("JKS");
				//
				//	InputStream ksInputStream = new FileInputStream(ShineGlobal.clientSSLPath); /// 证书存放地址
				//	ks.load(ksInputStream, password.toCharArray());
				//	//KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
				//	//String str=TrustManagerFactory.getDefaultAlgorithm();
				//	String algorithm="SunX509";
				//
				//	TrustManagerFactory tmf=TrustManagerFactory.getInstance(algorithm);
				//
				//	_clientCX = SSLContext.getInstance("TLS");
				//	tmf.init(ks);
				//
				//	//设置信任证书
				//	TrustManager[] trustManagers = tmf == null ? null : tmf.getTrustManagers();
				//	_clientCX.init(null, trustManagers, null);
				//}
				//catch(Exception e)
				//{
				//	e.printStackTrace();
				//}
				
				
				try
				{
					//String password="netty123";
					
					String password="mypassword";
					
					KeyStore ks=KeyStore.getInstance("JKS");
					
					InputStream ksInputStream = new FileInputStream(ShineGlobal.serverSSLPath); /// 证书存放地址
					ks.load(ksInputStream, password.toCharArray());
					//KeyManagerFactory充当基于密钥内容源的密钥管理器的工厂。
					//String algorithm=KeyManagerFactory.getDefaultAlgorithm();
					String algorithm="SunX509";
					
					KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);//getDefaultAlgorithm:获取默认的 KeyManagerFactory 算法名称。
					kmf.init(ks, password.toCharArray());
					//SSLContext的实例表示安全套接字协议的实现，它充当用于安全套接字工厂或 SSLEngine 的工厂。
					SSLContext sslContext = SSLContext.getInstance("TLS");
					sslContext.init(kmf.getKeyManagers(), null, null);
					
					_serverCX=sslContext;
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	/** 添加客户端sslHandler */
	public static void addClientSSLHandler(ChannelPipeline pipeline)
	{
		SSLEngine engine=_clientCX.createSSLEngine();
		engine.setNeedClientAuth(false);
		engine.setUseClientMode(true);
		pipeline.addLast(new SslHandler(engine));
	}
	
	/** 添加服务器sslHandler */
	public static void addServerSSLHandler(ChannelPipeline pipeline)
	{
		if(ShineSetting.sslUseSelfSigned)
		{
			SSLEngine engine=_serverSslCtx.newEngine(PooledByteBufAllocator.DEFAULT);
			engine.setNeedClientAuth(false);
			engine.setUseClientMode(false);
			pipeline.addLast(new SslHandler(engine));
		}
		else
		{
			SSLEngine engine=_serverCX.createSSLEngine();
			engine.setNeedClientAuth(false);
			engine.setUseClientMode(false);
			pipeline.addLast(new SslHandler(engine));
		}
	}
}
