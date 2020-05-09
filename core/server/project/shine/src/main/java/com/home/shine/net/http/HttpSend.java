package com.home.shine.net.http;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.net.socket.BytesBuffer;
import com.home.shine.support.collection.SMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

/** http发送 */
public abstract class HttpSend
{
	public int ioIndex;
	
	private URI _uri;
	private int _method;
	/** http头 */
	private SMap<CharSequence,Object> _headers;
	
	private String _host;
	private int _port;
	
	private Channel _ch;
	
	/** 重试次数 */
	private int _tryTimes=0;
	
	private NettyHttpInitializer _initializer=new NettyHttpInitializer();
	
	/** 是否为https消息 */
	private boolean _isHttps=false;
	/** 是否为客户端发送 */
	private boolean _isClient=false;
	
	private boolean _needBase64=false;
	
	public HttpSend()
	{
		ioIndex=this.hashCode() & ThreadControl.ioThreadNumMark;
	}
	
	/** 是否为客户端连接 */
	public void setIsClient(boolean value)
	{
		_isClient=value;
	}
	
	/** 设置是否https消息 */
	public void setIsHttps(boolean value)
	{
		_isHttps=value;
	}
	
	public void setNeedBase64(boolean value)
	{
		_needBase64=value;
	}
	
	/** 发送 */
	public void send(String url)
	{
		send(url,HttpMethodType.Get,null);
	}
	
	/** 发送 */
	public void send(String url,int method,SMap<CharSequence,Object> headers)
	{
		try
		{
			_uri=new URI(url);
		}
		catch(URISyntaxException e)
		{
			Ctrl.errorLog(e);
		}
		
		_host=_uri.getHost();
		_port=_uri.getPort();
		
		//默认80端口
		if(_port==-1)
		{
			_port=_isHttps ? 443 : 80;
		}
		
		_method=method;
		_headers=headers;
		
		ThreadControl.addIOFuncAbs(ioIndex,this::toConnect);
	}
	
	/** 连接(io线程) */
	private void toConnect()
	{
		try
		{
			_ch=NettyGroup.createSendHttp(_host,_port,_isClient,_initializer,k->
			{
				if(k.isSuccess())
				{
					preConnectSuccess();
				}
				else
				{
					Ctrl.log("http连接失败,cause:",k.cause().getMessage());
					
					ThreadControl.addOtherIOFunc(ioIndex,this::preConnectFailed);
				}
			});
			
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			
			preConnectFailed();
			return;
		}
	}
	
	/** 预备连接失败(io线程) */
	private void preConnectFailed()
	{
		close();
		
		if(++_tryTimes>=ShineSetting.httpSendTryTimes)
		{
			doConnectFailed();
		}
		else
		{
			toConnect();
		}
	}
	
	private void doConnectFailed()
	{
		try
		{
			onConnectFailed();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
	}
	
	/** 连接失败(通信线程(netty)) */
	public abstract void onConnectFailed();
	
	/** 关闭(io线程) */
	public void close()
	{
		if(_ch!=null)
		{
			_ch.close();
			_ch=null;
		}
	}
	
	/** 预备收到数据(netty线程) */
	private void preReceiveData(BytesReadStream stream)
	{
		ThreadControl.addOtherIOFunc(ioIndex,()->
		{
			onReceiveData(stream);
		});
	}
	
	/** 收到数据(IO线程) */
	public abstract void onReceiveData(BytesReadStream stream);
	
	/** 预备连接成功(netty线程) */
	protected void preConnectSuccess()
	{
		ThreadControl.addOtherIOFunc(ioIndex,this::connectSuccess);
	}
	
	/** 写发送流 */
	public abstract void writeToStream(BytesWriteStream stream);
	
	/** 连接上(io线程) */
	private void connectSuccess()
	{
		//已关闭
		if(_ch==null)
			return;
		
		HttpMethod mm;
		
		if(_method==HttpMethodType.Post)
		{
			mm=HttpMethod.POST;
		}
		else
		{
			mm=HttpMethod.GET;
		}
		
		DefaultFullHttpRequest request;
		
		String query=_uri.getRawQuery();
		
		String str;
		
		if(query!=null && !query.isEmpty())
		{
			str=_uri.getPath() + "?" + query;
		}
		else
		{
			str=_uri.getPath();
		}
		
		String contentType;
		
		if(_headers!=null && _headers.contains(HttpHeaderNames.CONTENT_TYPE))
		{
			contentType=(String)_headers.remove(HttpHeaderNames.CONTENT_TYPE);
		}
		else
		{
			contentType=HttpContentType.Application;
		}
		
		HttpHeaders hds;
		
		//get
		if(_method==HttpMethodType.Get)
		{
			request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,mm,str);
			hds=request.headers();
			hds.set(HttpHeaderNames.HOST,_host);
			hds.set(HttpHeaderNames.CONTENT_TYPE,contentType);
			hds.set(HttpHeaderNames.CONTENT_LENGTH,0);
		}
		//post
		else
		{
			//TODO:回头换写法
			BytesWriteStream stream=BytesWriteStream.create();
			writeToStream(stream);
			
			int len=stream.length();
			
			if(len>=ShineSetting.getMsgBufSize(_isClient,false))
			{
				Ctrl.warnLog("request长度超出上限",len);
			}
			
			ByteBuf buf=null;
			
			if(_needBase64)
			{
				byte[] decode=Base64.getEncoder().encode(stream.getByteArray());
				len=decode.length;
				buf=PooledByteBufAllocator.DEFAULT.ioBuffer(len);
				buf.writeBytes(decode,0,len);
			}
			else
			{
				buf=PooledByteBufAllocator.DEFAULT.ioBuffer(len);
				buf.writeBytes(stream.getBuf(),0,len);
			}
			
			request=new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,mm,str,buf);
			hds=request.headers();
			hds.set(HttpHeaderNames.HOST,_host);
			hds.set(HttpHeaderNames.CONTENT_TYPE,contentType);
			hds.set(HttpHeaderNames.CONTENT_LENGTH,len);
		}
		
		if(_headers!=null)
		{
			_headers.forEach((k,v)->
			{
				hds.set(k,v);
			});
		}
		
		_ch.writeAndFlush(request);
	}
	
	public class NettyHttpInitializer extends ChannelInitializer<Channel>
	{
		@Override
		public void initChannel(Channel ch) throws Exception
		{
			ChannelPipeline pipeline=ch.pipeline();
			
			if(_isHttps)
			{
				NettyGroup.addClientSSLHandler(pipeline);
			}
			
			//pipeline.addLast("inflater",new HttpContentDecompressor());
			//pipeline.addLast("aggregator",new HttpObjectAggregator(1048576));
			pipeline.addLast("encoder",new HttpRequestEncoder());
			pipeline.addLast("decoder",new HttpResponseDecoder());
			pipeline.addLast("handler",new NettyHttpHandler());
		}
	}
	
	public class NettyHttpHandler extends SimpleChannelInboundHandler<Object>
	{
		private boolean _hasLength;
		
		private int _lastLength=-1;
		
		private BytesBuffer _buffer;
		
		public NettyHttpHandler()
		{
		
		}

		@Override
		protected void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception
		{
			if(msg instanceof HttpResponse)
			{
				HttpResponse response=(HttpResponse)msg;
				
				String len=response.headers().get(HttpHeaderNames.CONTENT_LENGTH);
				
				if(len==null)
				{
					_hasLength=false;
					_lastLength=0;
				}
				else
				{
					_hasLength=true;
					_lastLength=Integer.parseInt(len);
				}
				
				_buffer=new BytesBuffer();
				
			}
			else if(msg instanceof HttpContent)
			{
				HttpContent cc=(HttpContent)msg;
				
				ByteBuf buf=cc.content();
				
				int bLen=buf.readableBytes();
				
				if(_hasLength)
				{
					_lastLength-=buf.readableBytes();
				}
				
				_buffer.append(buf);
				
				//够了
				if(_hasLength)
				{
					if(_lastLength<=0)
					{
						preReceiveData(_buffer.createReadStream());
					}
				}
				else
				{
					//空的
					if(bLen==0)
					{
						preReceiveData(_buffer.createReadStream());
					}
				}
			}
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)
		{
			//先无视
			//			_http.onIOError();
			
			Ctrl.warnLog("nettyError",cause.getMessage());
			Ctrl.printStackTrace(cause.getStackTrace());
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
			
			ctx.channel().close();
		}
	}
}
