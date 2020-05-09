package com.home.shine.net.http;

import com.home.shine.bytes.BytesReadStream;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.HttpContentType;
import com.home.shine.constlist.HttpMethodType;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.SMap;
import com.home.shine.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;

/** http接收对象 */
public class HttpReceive
{
	/** io序号 */
	public int ioIndex;
	
	private Channel _channel;
	
	//msg
	
	/** 方法 */
	public int method=HttpMethodType.Get;
	
	//url部分
	/** 源地址(url=path+?+args)(并且url第一字符为/,如/check?a=1&b=2) */
	public String url;
	/** 路径 */
	public String path="";
	/** 参数(原生参数) */
	public String argsStr="";
	/** 参数(get参数) */
	public SMap<String,String> args;
	/** 头信息 */
	public SMap<String,String> headers;
	
	//post部分
	/** 数据流(post时才有值) */
	public BytesReadStream postStream=null;
	//	/** 数据(post时才有值) */
	//	public String data=null;
	
	/** 存在时间(ms) */
	public int timeOut=ShineSetting.httpRequestKeepTime;
	
	//address
	
	private InetSocketAddress _address;
	/** ip记录 */
	private String _remoteIP="";
	/** 端口 */
	private int _remotePort=-1;
	
	/** 是否回复过 */
	private volatile boolean _responsed=false;
	
	private volatile boolean _disposed=false;
	
	public HttpReceive(Channel channel)
	{
		_channel=channel;
		
		ioIndex=this.hashCode() & ThreadControl.ioThreadNumMark;
	}
	
	/** io初始化(io线程) */
	public void ioInit()
	{
		//先添加
		ThreadControl.getIOThread(ioIndex).addHttpReceive(this);
	}
	
	/** io析构(io线程) */
	public void ioDispose()
	{
		ThreadControl.getIOThread(ioIndex).removeHttpReceive(this);
	}
	
	/** 获取query字符串 */
	public String getQueryString()
	{
		if(args==null)
		{
			return "";
		}
		
		return StringUtils.setWebArgs(args,false);
	}
	
	//address
	
	private InetSocketAddress getRemoteAddress()
	{
		if(_address==null)
		{
			if(_channel!=null)
			{
				_address=(InetSocketAddress)_channel.remoteAddress();
			}
		}
		
		return _address;
	}
	
	/** 获取连接目标的IP(用的是HostString) */
	public String remoteIP()
	{
		if(_remoteIP.isEmpty())
		{
			InetSocketAddress addr=getRemoteAddress();
			
			if(addr!=null)
			{
				//				_remoteIP=addr.getHostName();
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
	
	//str

	/** 获取发送str */
	public String getPostStr()
	{
		if(!StringUtils.isNullOrEmpty(argsStr))
		{
			return argsStr;
		}
		
		if(postStream==null)
		{
			return "";
		}
		
		return argsStr = postStream.readUTFBytes(postStream.bytesAvailable());
	}
	
	//re
	
	/** 回复结果 */
	public void result(String str)
	{
		result(str,HttpContentType.Application);
	}
	
	/** 回复字符串 */
	public void result(String str,String contentType)
	{
		if(_channel==null)
			return;
		
		ByteBuf buf=Unpooled.copiedBuffer(str,CharsetUtil.UTF_8);
		
		toResultBuf(buf,contentType);
	}
	
	/** 回复stream(任意线程) */
	public void result(BytesWriteStream stream)
	{
		if(_channel==null)
			return;
		
		int len;
		ByteBuf buf=PooledByteBufAllocator.DEFAULT.ioBuffer(len=stream.length());
		buf.writeBytes(stream.getBuf(),0,len);
		toResultBuf(buf,HttpContentType.Application);
	}
	
	/** 回复stream(任意线程) */
	public void result(byte[] arr)
	{
		if(_channel==null)
			return;
		
		int len;
		ByteBuf buf=PooledByteBufAllocator.DEFAULT.ioBuffer(len=arr.length);
		buf.writeBytes(arr,0,len);
		toResultBuf(buf,HttpContentType.Application);
	}
	
	/** 回复空 */
	public void resultEmpty()
	{
		if(_channel==null)
		{
			return;
		}
		
		if(!_channel.isOpen())
		{
			return;
		}
		
		if(!_channel.isActive())
		{
			return;
		}
		
		sendResponse(HttpContentType.Application,null);
	}
	
	private void toResultBuf(ByteBuf buf,String contentType)
	{
		sendResponse(contentType,buf);
	}
	
	private synchronized void sendHttpResponse(HttpResponse response)
	{
		if(_responsed)
			return;
		
		_responsed=true;
		
		try
		{
			_channel.writeAndFlush(response);
			
			_channel.closeFuture().addListener(k->
			{
				if(!k.isSuccess())
				{
					Ctrl.warnLog("http回复失败");
				}
				
				dispose();
			});
		}
		catch(Exception e)
		{
			//出错
			dispose();
		}
	}
	
	private synchronized void sendResponse(String contentType,ByteBuf buf)
	{
		HttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,buf);
		
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,contentType);
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,buf==null ? 0 : buf.readableBytes());
		response.headers().add("Access-Control-Allow-Origin", "*");
		response.headers().add("Access-Control-Allow-Methods", "POST");
		response.headers().add("Access-Control-Allow-Headers", "accept, content-type");
		response.headers().add("Access-Control-Max-Age", "1728000");
		
		sendHttpResponse(response);
	}
	
	public synchronized void backOptions(HttpRequest request)
	{
		ByteBuf buf = Unpooled.copiedBuffer("Allow", CharsetUtil.UTF_8);
		HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
		String vlsOrigin = request.headers().get("ORIGIN");
		response.headers().set(HttpHeaderNames.CONTENT_TYPE,"application/x-www-form-urlencoded");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, 0 );
		response.headers().add("Access-Control-Allow-Origin", vlsOrigin);
		response.headers().add("Access-Control-Allow-Methods", "POST");
		response.headers().add("Access-Control-Allow-Headers", "accept, content-type");
		response.headers().add("Access-Control-Max-Age", "1728000");
		
		sendHttpResponse(response);
	}
	
	/** 析构 */
	public synchronized void disposeForIO()
	{
		if(_disposed)
			return;
		
		_disposed=true;
		
		try
		{
			if(_channel!=null)
			{
				_channel.close();
				_channel=null;
			}
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		ioDispose();
	}
	
	/** 析构 */
	public void dispose()
	{
		ThreadControl.addIOFuncAbs(ioIndex,this::disposeForIO);
	}
	
	//快捷方式
	
	public boolean hasArg(String str)
	{
		return args.get(str)!=null;
	}
	
	public boolean getBooleanArg(String str)
	{
		String value=args.get(str);
		
		if(value!=null)
			return value.equals("1");
		
		return false;
	}
	
	public int getIntArg(String str)
	{
		String value=args.get(str);
		
		if(value!=null)
			return Integer.parseInt(value);
		
		return 0;
	}
	
	public long getLongArg(String str)
	{
		String value=args.get(str);
		
		if(value!=null)
			return Long.parseLong(value);
		
		return 0L;
	}
}
