package com.home.shine.net.webSocket;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.NettyGroup;
import com.home.shine.net.socket.BaseSocketContent;
import com.home.shine.net.socket.ReceiveSocket;
import com.home.shine.net.socket.ServerSocket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

public abstract class WebServerSocket extends ServerSocket
{
	private boolean _needWSS=false;
	
	public WebServerSocket(int port)
	{
		super(port);
		
		_needWSS=ShineSetting.clientWebSocketUseWSS;
	}
	
	@Override
	protected void initReceiveSocket(ReceiveSocket socket)
	{
		super.initReceiveSocket(socket);
		socket.setIsWebSocket(true);
	}
	
	public static String getWebSocketLocation(boolean isSSL,String host)
	{
		String scheme=isSSL ? "wss" : "ss";
		return scheme+"://" + host+"/webSocket";
	}
	
	@Override
	protected void toInitChannel(ChannelPipeline pipeline)
	{
		if(_needWSS)
		{
			NettyGroup.addServerSSLHandler(pipeline);
		}

		pipeline.addLast(new HttpServerCodec(65536,65536,65536));
		pipeline.addLast(new HttpObjectAggregator(65536));
		pipeline.addLast(new NettyWebSocketServerHandler(this));
		
		//if(_sslEngine!=null)
		//{
		//	pipeline.addLast(new AcceptorIdleStateTrigger());
		//}
	}
	
	/** 不解析的 */
	private class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<Object>
	{
		private WebServerSocket _server;
		
		private BaseSocketContent _content;

		private WebSocketServerHandshaker _handshaker;
		
		public NettyWebSocketServerHandler(WebServerSocket server)
		{
			_server=server;
		}
		
		@Override
		public void channelRegistered(ChannelHandlerContext ctx) throws Exception
		{
			ChannelConfig config=ctx.channel().config();
			
			DefaultSocketChannelConfig socketConfig=(DefaultSocketChannelConfig)config;
			
			//socketConfig.setAllocator(NettyGroup.heapBufAllocator);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);

			//ReceiveSocket socket=_server.createReceiveSocket();
			//initReceiveSocket(socket);
			//_content=socket.initReceive(ctx.channel(),_server);
			//_server.preNewReceiveSocket(socket);
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
			if (arg instanceof FullHttpRequest) {
				handleHttpRequest(ctx, (FullHttpRequest) arg);
			} else if (arg instanceof WebSocketFrame) {
				handleWebSocketFrame(ctx, (WebSocketFrame) arg);
			}

		}

		@Override
		public void channelReadComplete(ChannelHandlerContext ctx) {
			ctx.flush();
		}

		private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
			// Handle a bad request.
			if (!req.decoderResult().isSuccess()) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
				return;
			}

			// Allow only GET methods.
			if (req.method() != HttpMethod.GET) {
				sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
				return;
			}

//			// Send the demo page and favicon.ico
//			if ("/".equals(req.uri())) {
//				ByteBuf content = WebSocketServerBenchmarkPage.getContent(getWebSocketLocation(req));
//				FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, OK, content);
//
//				res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
//				HttpUtil.setContentLength(res, content.readableBytes());
//
//				sendHttpResponse(ctx, req, res);
//				return;
//			}
//
//			if ("/favicon.ico".equals(req.uri())) {
//				FullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
//				sendHttpResponse(ctx, req, res);
//				return;
//			}

			// Handshake
			WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
					getWebSocketLocation(_needWSS,req.headers().get(HttpHeaderNames.HOST)), null, true, 16 * 1024 * 1024);
			_handshaker = wsFactory.newHandshaker(req);

			if (_handshaker == null) {
				WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
			} else {
				_handshaker.handshake(ctx.channel(), req);
				
				//此时再视为连接完成
				ReceiveSocket socket=_server.createReceiveSocket();
				initReceiveSocket(socket);
				_content=socket.initReceive(ctx.channel(),_server);
				_server.preNewReceiveSocket(socket);
			}
			
			
		}

		private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
			// Check for closing frame
			if (frame instanceof CloseWebSocketFrame) {
				_handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
				return;
			}
			if (frame instanceof PingWebSocketFrame) {
				ctx.write(new PongWebSocketFrame(frame.content().retain()));
				return;
			}
			
			_content.onBuffer(frame.content());

//			if (frame instanceof TextWebSocketFrame) {
//				// Echo the frame
//				ctx.write(frame.retain());
//				return;
//			}
//			if (frame instanceof BinaryWebSocketFrame) {
//				// Echo the frame
//				ctx.write(frame.retain());
//			}
		}

		private void sendHttpResponse(
				ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
			// Generate an error page if response getStatus code is not OK (200).
			if (res.status().code() != 200) {
				ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
				res.content().writeBytes(buf);
				buf.release();
				HttpUtil.setContentLength(res, res.content().readableBytes());
			}

			// Send the response and close the connection if necessary.
			ChannelFuture f = ctx.channel().writeAndFlush(res);
			if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			
			//Ctrl.print(cause.getMessage());
			//cause.printStackTrace();
			//ctx.close();
		}
	}
	
	
}
