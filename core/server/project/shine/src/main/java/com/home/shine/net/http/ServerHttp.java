package com.home.shine.net.http;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.net.socket.BytesBuffer;
import com.home.shine.support.collection.SMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 服务器http接收 */
public abstract class ServerHttp
{
	/** 端口 */
	private int _port;
	
	/** 是否启用https */
	private boolean _isHttps=false;
	/** 是否为客户端端口 */
	private boolean _isClient=false;
	
	//netty
	
	private ServerBootstrap _sb;
	
	private Channel _ch;
	
	/** 是否需要base64解码 */
	private boolean _needBase64=false;
	
	public ServerHttp(int port)
	{
		_port=port;
	}
	
	/** 是否为客户端端口(影响bufSize) */
	public void setIsClient(boolean value)
	{
		_isClient=value;
	}
	
	/** 是否https */
	public void setIsHttps(boolean value)
	{
		_isHttps=value;
	}
	
	public void start()
	{
		Ctrl.log("启动http端口",_port);
		
		int sendBufSize=_isClient ? ShineSetting.clientHttpSendBuf : ShineSetting.serverHttpSendBuf;
		int receiveBufSize=_isClient ? ShineSetting.clientHttpReceiveBuf : ShineSetting.serverHttpReceiveBuf;
		
		_sb=new ServerBootstrap();
		_sb.group(NettyGroup.getBossGroup(),NettyGroup.getWorkerGroup());
		
		_sb.channel(NettyGroup.EpollAvailable ? EpollServerSocketChannel.class :NioServerSocketChannel.class);
		
		
		_sb.childHandler(new NettyHttpServerInitializer(this));
		
		_sb.option(ChannelOption.SO_BACKLOG,ShineSetting.nettyBackLog);
		
		_sb.childOption(ChannelOption.SO_REUSEADDR,true);
		_sb.childOption(ChannelOption.TCP_NODELAY,true);
		_sb.childOption(ChannelOption.SO_KEEPALIVE,true);
		_sb.childOption(ChannelOption.SO_SNDBUF,sendBufSize);
		_sb.childOption(ChannelOption.SO_RCVBUF,receiveBufSize);
		_sb.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,60 * 1000);
		
		//_sb.childOption(ChannelOption.SO_LINGER,0);
		
		try
		{
			_ch=_sb.bind(_port).sync().channel();
		}
		catch(Exception e)
		{
			onStartFailed();
		}
	}
	
	/** 关闭端口 */
	public void close()
	{
		if(_sb!=null)
		{
			if(_ch!=null)
			{
				_ch.close();
				_ch=null;
			}
			
			_sb=null;
		}
	}
	
	/** 启动失败 */
	public abstract void onStartFailed();
	
	/** http消息(netty线程) */
	private void preHttpReceive(HttpReceive receive)
	{
		ThreadControl.addOtherIOFunc(receive.ioIndex,()->
		{
			onHttpReceive(receive);
		});
	}
	
	/** http收到数据(IO线程) */
	public abstract void onHttpReceive(HttpReceive receive);
	
	public boolean needBase64()
	{
		return _needBase64;
	}
	
	public void setNeedBase64(boolean value)
	{
		_needBase64=value;
	}
	
	private class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel>
	{
		private ServerHttp _server;

		public NettyHttpServerInitializer(ServerHttp server)
		{
			_server=server;
		}

		@Override
		protected void initChannel(SocketChannel paramC) throws Exception
		{
			ChannelPipeline pipeline=paramC.pipeline();
			
			if(_isHttps)
			{
				NettyGroup.addServerSSLHandler(pipeline);
			}
			
			pipeline.addLast("encoder",new HttpResponseEncoder());
			pipeline.addLast("decoder",new HttpRequestDecoder());
			pipeline.addLast("handler",new NettyHttpServerHandler(_server));
		}
	}
	
	private class NettyHttpServerHandler extends SimpleChannelInboundHandler<Object>
	{
		private ServerHttp _server;
		
		/** 缓存的消息 */
		protected HttpReceive _receive;
		
		private boolean _hasLength;
		
		private int _lastLength;
		
		protected BytesBuffer _buffer;
		
		public NettyHttpServerHandler(ServerHttp server)
		{
			_server=server;
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);
			
			//_receive=new HttpReceive(ctx.channel());
			
			//ThreadControl.addOtherIOFunc(_receive.ioIndex,()->
			//{
			//	_receive.ioInit();
			//});
			
			
			//_server.preHttpReceive(_receive);
		}
		
		@Override
		protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception
		{
			if(msg instanceof HttpRequest)
			{
				HttpRequest request=(HttpRequest)msg;
				
				//该对象只有构造传值,所以没法缓存了。
				QueryStringDecoder qd=new QueryStringDecoder(request.uri());
				
				String path=qd.path();
				
				if(path.startsWith("/"))
				{
					path=path.substring(1,path.length());
				}
				
				//坏消息
				if(path.equals("bad-request"))
				{
					Ctrl.warnLog("收到坏消息");
				}
				else
				{
					SMap<String,String> args=new SMap<>(qd.parameters().size());
					
					for(Entry<String,List<String>> kv : qd.parameters().entrySet())
					{
						args.put(kv.getKey(),kv.getValue().get(0));
					}
					
					SMap<String,String> headers=new SMap<>(request.headers().size());
					
					Iterator<Map.Entry<String,String>> it=request.headers().entries().iterator();
					
					while(it.hasNext())
					{
						Map.Entry<String,String> heardParam=it.next();
						
						headers.put(heardParam.getKey(),heardParam.getValue());
					}
					
					_receive=new HttpReceive(ctx.channel());
					
					ThreadControl.addOtherIOFunc(_receive.ioIndex,()->
					{
						_receive.ioInit();
					});
					
					_receive.url=request.uri();
					_receive.path=path;
					_receive.args=args;
					_receive.headers=headers;
					
					if(request.method()==HttpMethod.GET)
					{
						_receive.method=HttpMethodType.Get;
					}
					else if(request.method()==HttpMethod.POST)
					{
						_receive.method=HttpMethodType.Post;
					}
					else if(request.method()==HttpMethod.OPTIONS)
					{
						_receive.backOptions(request);
						return ;
					}
					
					if(_receive.method==HttpMethodType.Get)
					{
						msgOver();
					}
					else
					{
						String len=request.headers().get(HttpHeaderNames.CONTENT_LENGTH);
						
						if(len==null)
						{
							_hasLength=false;
							_lastLength=0;
						}
						else
						{
							_hasLength=true;
							_lastLength=Integer.parseInt(len);
							
							//超上限
							if(_lastLength>=ShineSetting.httpBufferSizeMax)
							{
								Ctrl.warnLog("收到超过上限的http消息",_lastLength);
								_hasLength=false;
								_lastLength=0;
								return;
							}
						}
						
						_buffer=new BytesBuffer();
					}
				}
			}
			else if(msg instanceof HttpContent)
			{
				//是post
				if(_receive!=null && _receive.method==HttpMethodType.Post)
				{
					//不合法的
					if(_buffer==null)
						return;
					
					HttpContent cc=(HttpContent)msg;
					
					ByteBuf buf=cc.content();
					
					int bLen=buf.readableBytes();
					
					if(_hasLength)
					{
						_lastLength-=bLen;
					}
					
					_buffer.append(buf);
					
					//TODO:这里要不要release?
					
					//收完
					if(_hasLength)
					{
						if(_lastLength<=0)
						{
							contentOver();
						}
					}
					else
					{
						//空消息
						if(bLen==0)
						{
							contentOver();
						}
					}
				}
			}
		}
		
		private void contentOver()
		{
			if(_server.needBase64())
			{
				_receive.postStream=BytesReadStream.create(Base64.getDecoder().decode(_buffer.createReadStream().getByteArray()));
			}
			else
			{
				_receive.postStream=_buffer.createReadStream();
			}
			
			msgOver();
		}
		
		private void msgOver()
		{
			_server.preHttpReceive(_receive);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)
		{
		
		}
		
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
			
			_receive.dispose();
		}
	}
}
