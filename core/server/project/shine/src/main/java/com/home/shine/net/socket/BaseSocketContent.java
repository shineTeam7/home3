package com.home.shine.net.socket;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/** 连接内容(tcp) */
public class BaseSocketContent
{
	protected BaseSocket _parent;
	
	protected int _ioIndex;
	
	private boolean _isWebSocket;
	
	/** 当前操作数 */
	private int _doIndex;
	
	/** netty连接上下文 */
	protected Channel _channel;
	
	/** 接收缓冲 */
	private LengthBasedFrameBytesBuffer _receiveBuffer;
	/** 是否已关闭 */
	private boolean _clozed=false;
	
	private static AtomicInteger _aa=new AtomicInteger();
	
	public BaseSocketContent(BaseSocket socket)
	{
		//不是客户端的可以涨
		_receiveBuffer=new LengthBasedFrameBytesBuffer(socket.getBufSize(),socket.getNeedBufGrow())
		{
			@Override
			protected void onePiece(byte[] bytes,int pos,int len)
			{
				if(!checkIsCurrent())
					return;
				
				//ByteBuf buffer=PooledByteBufAllocator.DEFAULT.buffer(len);
				//buffer.writeBytes(bytes,pos,len);
				//buffer.resetReaderIndex();
				
				byte[] arr=new byte[len];
				System.arraycopy(bytes,pos,arr,0,len);
				
				ThreadControl.addIOFuncAbs(_ioIndex,()->
				{
					if(!checkIsCurrent())
					{
						//buffer.release();
						return;
					}
					
					_parent.onePiece(arr);
				});
			}
			
			@Override
			protected void onError(String msg)
			{
				Ctrl.warnLog("socket解析出错被关闭:",msg);

				ThreadControl.addIOFuncAbs(_ioIndex,()->
				{
					if(!checkIsCurrent())
						return;
					
					_parent.closeForIO(BaseSocket.Close_Error);
				});
			}
		};
		
		setParent(socket);
	}
	
	/** 改变归属 */
	public void setParent(BaseSocket socket)
	{
		_parent=socket;
		_ioIndex=socket.ioIndex;
		_isWebSocket=socket.isWebSocket();
	}
	
	public void setChannel(Channel channel)
	{
		_channel=channel;
	}
	
	public void setDoIndex(int value)
	{
		_doIndex=value;
	}
	
	public int getDoIndex()
	{
		return _doIndex;
	}
	
	/** 检查是否是当前的 */
	protected boolean checkIsCurrent()
	{
		return _parent.getDoIndex()==_doIndex;
	}
	
	/** 收到buffer(netty线程) */
	public void onBuffer(ByteBuf buf)
	{
		if(!checkIsCurrent())
			return;
		
		_receiveBuffer.append(buf);
	}
	
	/** 连接被关闭(netty线程) */
	public void preClosed()
	{
		if(!checkIsCurrent())
			return;
		
		ThreadControl.addOtherIOFunc(_ioIndex,()->
		{
			if(!checkIsCurrent())
				return;
			
			_parent.closeForIO(BaseSocket.Close_ChannelInactive);
		});
	}
	
	/** 预备收数据(不解析用)(netty线程) */
	//关注点  转到IO线程处理
	public void preSocketData(byte[] bb)
	{
		if(!checkIsCurrent())
			return;
		
		ThreadControl.addOtherIOFunc(_ioIndex,()->
		{
			if(!checkIsCurrent())
				return;
			
			_parent.onSocketData(bb);
		});
	}
	
	/** 连接执行关闭(io线程) */
	public void close()
	{
		if(_clozed)
			return;
		
		_clozed=true;
		
		doClose();
	}
	
	protected void doClose()
	{
		if(_channel!=null)
		{
			_channel.close();
			_channel=null;
		}
	}
	
	/** 获取连接地址 */
	public InetSocketAddress getRemoteAddress()
	{
		if(_channel!=null)
		{
			return (InetSocketAddress)_channel.remoteAddress();
		}
		
		return null;
	}
	
	/** 执行发送stream */
	public void doSendStream(BytesWriteStream stream)
	{
		if(_channel==null)
			return;
		
		int len;
		//ByteBuf byteBuf=NettyGroup.heapBufAllocator.ioBuffer(len=stream.length());
		ByteBuf byteBuf=PooledByteBufAllocator.DEFAULT.ioBuffer(len=stream.length());
		byteBuf.writeBytes(stream.getBuf(),0,len);
		
		if(_isWebSocket)
		{
			_channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf)).addListener(new ChannelFutureListener()
			{
				@Override
				public void operationComplete(ChannelFuture channelFuture) throws Exception
				{
					//失败一个
					if(!channelFuture.isSuccess())
					{
						Ctrl.print("发送一个失败",_aa.incrementAndGet());
						
						preClosed();
					}
				}
			});
		}
		else
		{
			_channel.writeAndFlush(byteBuf);
		}
	}
	
	protected void sendWebOnce(ByteBuf buf)
	{
		if(_channel==null)
			return;
		
		if(!checkIsCurrent())
			return;
		
		//_channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
		
	}
	
	/** 直接发送数组(cross用) */
	public void doSendByteArr(byte[] arr)
	{
		if(_channel==null)
			return;
		
		_channel.writeAndFlush(arr);
	}
}
