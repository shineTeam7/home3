using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace ShineEngine
{
    /** 连接实体 */
    public class TCPSocketContent : BaseSocketContent
    {
		public TCPSocketContent(BaseSocket socket,int index) : base(socket,index)
		{

		}

		/** 连接 */
		protected override Socket toCreateSocket(IPAddress hostAddress)
		{
			Socket socket=new Socket(hostAddress.AddressFamily,SocketType.Stream,ProtocolType.Tcp);
			socket.NoDelay=true;
			return socket;
		}

		protected override void beginReceive()
		{
			byte[] buf=_receiveBuffer.getBuf();
			int length=_receiveBuffer.getLength();
			int space=buf.Length - length;

			toBeginReceive(_receiveBuffer.getBuf(),length,space);
		}

		protected override bool onReceived(int len)
		{
			_parent.socketTick();
			_receiveBuffer.doReadLength(len);

			return true;
		}

		protected override void doClose()
		{
			try
			{
				_socket.Close();
			}
			catch(Exception e)
			{
				// Ctrl.printExceptionForIO(e);
			}
		}

		/** 发送一次 */
		public override void sendAsyncOnce(byte[] arr,int off,int len)
		{
			if(!checkIsCurrent())
				return;

			toBeginSend(arr,off,len);
		}


		//sync

		/** 推字节(IO线程) */
		public override void toSendStream(BytesWriteStream stream)
		{
			if(!checkIsCurrent())
				return;

			toSend(stream.getBuf(),0,stream.length());
		}

		protected override bool toReceiveLoopOnce()
		{
			return _receiveBuffer.readSocketOne(_socket);
		}
	}
}