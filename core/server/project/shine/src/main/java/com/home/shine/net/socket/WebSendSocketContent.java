package com.home.shine.net.socket;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.net.webSocket.WebServerSocket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class WebSendSocketContent extends SendSocketContent
{
	public WebSendSocketContent(SendSocket socket)
	{
		super(socket);
	}
	
	protected void toInitChannel(ChannelPipeline pipeline)
	{
		((WebSendSocket)_socket).initPipeline(pipeline);
		
		pipeline.addLast(new HttpClientCodec());
		pipeline.addLast(new HttpObjectAggregator(65536));
		//pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
		pipeline.addLast(new NettyWebSocketHandler());
	}
	
	/** 不解析的 */
	private class NettyWebSocketHandler extends SimpleChannelInboundHandler<Object>
	{
		private WebSocketClientHandshaker _handshaker;
		private ChannelPromise _handshakeFuture;
		
		public NettyWebSocketHandler()
		{
			boolean isSSl=ShineSetting.clientWebSocketUseWSS;
			
			try
			{
				URI uri=new URI(WebServerSocket.getWebSocketLocation(isSSl,_socket._host+":"+_socket._port));
				
				_handshaker=WebSocketClientHandshakerFactory.newHandshaker(uri,WebSocketVersion.V13,null,true,new DefaultHttpHeaders(),8*1024*1024);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause)
		{
			// Ctrl.throwError(cause.getMessage());
			// cause.printStackTrace();
			// ctx.close();
			
			//连接中的
			if(_socket.isConnecting())
			{
				preConnectFailed();
			}
		}
		
		@Override
		public void handlerAdded(ChannelHandlerContext ctx) {
			_handshakeFuture = ctx.newPromise();
		}
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception
		{
			ChannelConfig config=ctx.channel().config();
			
			DefaultSocketChannelConfig socketConfig=(DefaultSocketChannelConfig)config;
			
			//socketConfig.setAllocator(NettyGroup.heapBufAllocator);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) {
			_handshaker.handshake(ctx.channel());
			
			//preConnectSuccess(ctx.channel());
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
			
			preClosed();
		}
		
		@Override
		public void channelRead0(ChannelHandlerContext ctx,Object msg) throws Exception
		{
			Channel ch=ctx.channel();
			
			if(!_handshaker.isHandshakeComplete())
			{
				try
				{
					_handshaker.finishHandshake(ch,(FullHttpResponse)msg);
					//Ctrl.print("WebSocket Client connected!");
					_handshakeFuture.setSuccess();
				}
				catch(WebSocketHandshakeException e)
				{
					Ctrl.print("WebSocket Client failed to connect");
					_handshakeFuture.setFailure(e);
				}
				
				//捂手完成才可以
				preConnectSuccess(ctx.channel());
				
				return;
			}
			
			if(msg instanceof FullHttpResponse)
			{
				FullHttpResponse response=(FullHttpResponse)msg;
				throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
			}
			
			WebSocketFrame frame=(WebSocketFrame)msg;
			if(frame instanceof BinaryWebSocketFrame)
			{
				//TextWebSocketFrame textFrame=(TextWebSocketFrame)frame;
				//Ctrl.print("WebSocket Client received message: " + textFrame.text());
				onBuffer(frame.content());
			}
			else if(frame instanceof PongWebSocketFrame)
			{
				Ctrl.print("WebSocket Client received pong");
			}
			else if(frame instanceof CloseWebSocketFrame)
			{
				Ctrl.print("WebSocket Client received closing");
				ch.close();
				//preClosed();
			}
			else
			{
				Ctrl.print("不支持的frame类型",frame);
			}
		}
		
	}
}
