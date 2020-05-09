package com.home.shine.net.socket;

import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.NettyGroup;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class SendSocketContent extends BaseSocketContent
{
	protected SendSocket _socket;
	
	protected Channel _sendChannel;
	
	private NettySocketInitializer _initializer=new NettySocketInitializer();
	
	public SendSocketContent(SendSocket socket)
	{
		super(socket);
		
		_socket=socket;
	}
	
	@Override
	protected void doClose()
	{
		super.doClose();
		
		if(_sendChannel!=null)
		{
			_sendChannel.close();
			_sendChannel=null;
		}
	}
	
	/** 执行连接(io线程) */
	public void connect(String host,int port)
	{
		if(_sendChannel!=null)
		{
			close();
		}
		
		try
		{
			_sendChannel=NettyGroup.createSendSocket(host,port,_socket._isClient,_initializer,k->
			{
				if(!k.isSuccess())
				{
					preConnectFailed();
				}
				else
				{
				
				}
			});
		}
		catch(Exception e)
		{
			preConnectFailed();
		}
	}
	
	/** 连接成功(netty线程) */
	protected void preConnectSuccess(Channel channel)
	{
		ThreadControl.addOtherIOFunc(_ioIndex,()->
		{
			//还是连接中
			if(checkIsCurrent() && _socket.isConnecting())
			{
				connectSuccessForIO(channel);
			}
			else
			{
				if(channel!=null)
				{
					channel.close();
				}
				
				close();
			}
		});
	}
	
	/** 连接失败(netty线程) */
	protected void preConnectFailed()
	{
		ThreadControl.addIOFuncAbs(_ioIndex,()->
		{
			if(checkIsCurrent() && _socket.isConnecting())
			{
				_socket.preConnectFailedForIO();
			}
			else
			{
				close();
			}
		});
	}
	
	private void connectSuccessForIO(Channel channel)
	{
		setChannel(channel);
		
		_socket.preConnectSuccessForIO();
	}
	
	protected void toInitChannel(ChannelPipeline pipeline)
	{
		//if(_socket.isClient() && ShineSetting.useKCP)
		//{
		//	pipeline.addLast("decoder",new CustomHandlerDecoder());
		//}
		
		pipeline.addLast("decoder",new CustomHandlerDecoder());
		pipeline.addLast("encoder",new ByteBufEncoder());
	}
	
	private class NettySocketInitializer extends ChannelInitializer<Channel>
	{
		@Override
		protected void initChannel(Channel paramC) throws Exception
		{
			ChannelPipeline pipeline=paramC.pipeline();
			
			toInitChannel(pipeline);
			
		}
	}
	
	private class CustomHandlerDecoder extends MessageToMessageDecoder<ByteBuf>
	{
		public CustomHandlerDecoder()
		{
		
		}
		
		@Override
		protected final void decode(ChannelHandlerContext ctx,ByteBuf msg,List<Object> out) throws Exception
		{
			onBuffer(msg);
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
		
		//@Override
		//public void channelRegistered(ChannelHandlerContext ctx) throws Exception
		//{
		//	ChannelConfig config=ctx.channel().config();
		//
		//	DefaultSocketChannelConfig socketConfig=(DefaultSocketChannelConfig)config;
		//
		//	socketConfig.setPerformancePreferences(0,1,2);
		//
		//	socketConfig.setAllocator(PooledByteBufAllocator.DEFAULT);
		//}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception
		{
			super.channelActive(ctx);
			
			preConnectSuccess(ctx.channel());
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
}
