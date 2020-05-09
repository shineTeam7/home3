using System;
using System.Buffers;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Net.Sockets.Kcp;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace ShineEngine
{
    public class KcpSocketContent : BaseSocketContent
    {
        private Kcp _kcp;

        private byte[] _sendBuff;

        private MemOwner _sendMem;

        /** 接收暂存 */
        private byte[] _receiveCache;
        /** 接收暂存 */
        private byte[] _receiveCach2;

        private BytesWriteStream _sendStream=new BytesWriteStream();

        public KcpSocketContent(BaseSocket socket,int index) : base(socket,index)
        {

        }

        protected override Socket toCreateSocket(IPAddress hostAddress)
        {
            return new Socket(hostAddress.AddressFamily, SocketType.Dgram, ProtocolType.Udp);
        }

        protected override void onConnectSuccess()
        {
            _kcp = new Kcp(1000, new Handler(this));
            _kcp.NoDelay(1, 16, 2, 1);
            _kcp.WndSize(ShineSetting.kcpWndSize, ShineSetting.kcpWndSize);
            _kcp.SetMtu(ShineSetting.kcpMTU);

            _sendBuff=new byte[ShineSetting.kcpMTU];
            _sendMem=new MemOwner(_sendBuff);

            _receiveCache=new byte[ShineSetting.kcpMTU];

            _receiveCach2=new byte[ShineSetting.msgBufSize];

            base.onConnectSuccess();
        }

        protected override void doClose()
        {
            try
            {
                _socket.Close();
            }
            catch(Exception e)
            {
                Ctrl.printExceptionForIO(e);
            }

            try
            {
                _kcp.Dispose();
            }
            catch(Exception e)
            {
                Ctrl.printExceptionForIO(e);
            }
        }

        public override void toSendStream(BytesWriteStream stream)
        {
            _kcp.Send(new Span<byte>(stream.getBuf(),0,stream.length()));
        }

        protected override void beginReceive()
        {
            toBeginReceive(_receiveCache,0,_receiveCache.Length);
        }

        protected override bool onReceived(int len)
        {
            return toKCPReceive(len);
        }

        private bool toKCPReceive(int len)
        {
            int resInput = _kcp.Input(new Span<byte>(_receiveCache,0,len));


            if (resInput < 0)
            {
                Ctrl.warnLogForIO("ukcp input fail. res:" + resInput);
                return false;
            }

            int ps = _kcp.PeekSize();

            while(ps>0)
            {
                if(ps>_receiveCach2.Length)
                {
                    Ctrl.warnLogForIO("kcp Receive时,_receiveCach2超出预设尺寸",ps);
                    _receiveCach2=new byte[ps];
                }

                int ps2=_kcp.Recv(_receiveCach2);

                if(ps2>0)
                {
                    _receiveBuffer.append(_receiveCach2, 0, ps2);
                    ps=_kcp.PeekSize();
                }
                else
                {
                    Ctrl.warnLogForIO("ukcp recv fail. res:" + resInput);
                    return false;
                }
            }

            return true;
        }

        protected override bool toReceiveLoopOnce()
        {
            int len;

            try
            {
                len=_socket.Receive(_receiveCache,0,_receiveCache.Length,SocketFlags.None);
            }
            catch(Exception e)
            {
                // Ctrl.printExceptionForIO(e);
                return false;
            }

            if(len>0)
            {
                return toKCPReceive(len);
            }
            else
            {
                return true;
            }
        }

        public override void onKcpLoop()
        {
            if(!checkIsCurrent())
                return;

            if(!_socket.Connected)
                return;

            _parent.runSendQueue();

            if(!isSending())
            {
                try
                {
                    _kcp.Update(DateTime.UtcNow);
                }
                catch(Exception e)
                {
                    Ctrl.printExceptionForIO(e);
                }
            }

            // int ps = _kcp.PeekSize();
            //
            // while(ps>0)
            // {
            //     int ps2=_kcp.Recv(_receiveCach2);
            //
            //     _receiveBuffer.append(_receiveCach2, 0, ps2);
            //     ps=_kcp.PeekSize();
            // }
        }

        public override void kcpSendLoop()
        {
            if(!checkIsCurrent())
                return;

            if(!_socket.Connected)
                return;

            lock(_sendStream)
            {
                if(_sendStream.length()>0)
                {
                    toSend(_sendStream.getBuf(),0,_sendStream.length());
                    _sendStream.clear();
                }
            }
        }

        private class MemOwner:IMemoryOwner<byte>
        {
            public Memory<byte> mem;

            public MemOwner(byte[] bytes)
            {
                mem=new Memory<byte>(bytes);
            }

            public void Dispose()
            {

            }

            public Memory<byte> Memory
            {
                get {return mem;}
            }
        }

        private class Handler: IKcpCallback
        {
            private KcpSocketContent _content;

            public Handler(KcpSocketContent content)
            {
                _content=content;
            }

            public void Output(IMemoryOwner<byte> buffer,int avalidLength)
            {
                if(!_content.checkIsCurrent())
                    return;

                byte[] arr;

                if(buffer==_content._sendMem)
                {
                    arr=_content._sendBuff;
                }
                else
                {
                    arr=buffer.Memory.ToArray();
                }

                if(ShineSetting.socketUseAsync)
                {
                    _content.toBeginSend(arr,0,avalidLength);
                }
                else
                {
                    lock(_content._sendStream)
                    {
                        _content._sendStream.writeByteArr(arr,0,avalidLength);
                    }
                }
            }

            public IMemoryOwner<byte> RentBuffer(int length)
            {
                return _content._sendMem;
            }
        }
    }
}
