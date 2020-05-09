using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;

namespace ShineEngine
{
    public abstract class BaseSocketContent
    {
        protected BaseSocket _parent;
        /** 当前操作数 */
        public int doIndex;

        protected Socket _socket;

        /** 接收缓冲 */
        protected LengthBasedFrameBytesBuffer _receiveBuffer;

        /** 是否已关闭 */
        protected bool _clozed = false;

        /** 发送中(异步用) */
        protected volatile bool _sending=false;
        public BaseSocketContent(BaseSocket baseSocket,int index)
        {
            _parent = baseSocket;
            this.doIndex=index;
        }

        public bool isSending()
        {
            return _sending;
        }

        /** 检查是否是当前的 */
        public bool checkIsCurrent()
        {
            return _parent.getDoIndex() == doIndex;
        }

        /** 连接失败一次(主线程) */
        protected void connectFailedOnce()
        {
            if (!checkIsCurrent())
                return;

            //不在连接中
            if (_parent.getState() != BaseSocket.Connecting)
                return;

            _parent.preConnectFailedForIO();
        }

        /** 读取一个(IO线程) */
        protected void onPiece(byte[] bytes,int pos,int len)
        {
            if (!checkIsCurrent())
                return;

            _parent.onePiece(bytes,pos,len);
        }

        /** 读失败(IO线程) */
        protected void onError(string msg)
        {
            Ctrl.printForIO("socket读取失败:" + msg);

            preBeClose(2);
        }

        /** 即将关闭(IO线程) */
        protected void preBeClose(int way)
        {
            // Ctrl.debugLogForIO("preBeClose",way);

            doClose();

            if (_clozed)
                return;

            _clozed = true;

            Ctrl.debugLogForIO("preBeClose2", way,this.GetHashCode());

            ThreadControl.addMainFunc(() =>
            {
                if (checkIsCurrent())
                {
                    _parent.beClose();
                }
            });
        }

        public void close()
        {
            if (_clozed)
                return;

            _clozed = true;

            Ctrl.debugLogForIO("socketContentClose");

            doClose();
        }

        /** 主线程 */
        protected void connectSuccess()
        {
            if (!checkIsCurrent())
                return;

            //不在连接中
            if (_parent.getState() != BaseSocket.Connecting)
                return;

            if (_socket.Connected)
            {
                onConnectSuccess();

                _parent.preConnectSuccessForIO();
            }
            else
            {
                //依靠自行timeOut
                connectFailedOnce();
            }
        }

        protected virtual void onConnectSuccess()
        {
            _receiveBuffer = new LengthBasedFrameBytesBuffer(ShineSetting.msgBufSize);
            _receiveBuffer.setPieceCall(this.onPiece);
            _receiveBuffer.setErrorCall(this.onError);

            if (ShineSetting.socketUseAsync)
            {
                beginReceive();
            }
        }

        protected abstract Socket toCreateSocket(IPAddress hostAddress);

        public void connect(IPAddress hostAddress, int port)
        {
            if (ShineSetting.openCheck && _socket != null)
            {
                Ctrl.errorLog("连接时,不该已存在socket");
            }

            _socket=toCreateSocket(hostAddress);
            _socket.SendBufferSize = ShineSetting.socketSendBufSize;
            _socket.ReceiveBufferSize = ShineSetting.socketReceiveBufSize;

            Ctrl.log("socketContent发起一次连接",doIndex,hostAddress);

            _socket.BeginConnect(hostAddress, port, onSocketConnect, this);
        }

        private void onSocketConnect(IAsyncResult result)
        {
            bool isError=false;

            try
            {
                _socket.EndConnect(result);
            }
            catch(Exception e)
            {
                // Ctrl.printExceptionForIO(e);
                isError=true;
            }

            try
            {
                EndPoint localEndPoint=_socket.LocalEndPoint;

                if(localEndPoint!=null)
                {
                    Ctrl.debugLogForIO("本地端口:",localEndPoint.Serialize().ToString(),doIndex);
                }
            }
            catch(Exception e)
            {
                Ctrl.printExceptionForIO(e);
            }

            Ctrl.debugLogForIO("socketContent连接结果,success:",!isError,this.GetHashCode());

            ThreadControl.addMainFunc(()=>
            {
                if(isError)
                {
                    connectFailedOnce();
                }
                else
                {
                    connectSuccess();
                }
            });
        }

        protected abstract void beginReceive();

        protected void toBeginSend(byte[] buf,int off,int len)
        {
            _sending=true;

            try
            {
                _socket.BeginSend(buf,off,len,SocketFlags.None,ar=>
                {
                    int rlen=0;

                    try
                    {
                        rlen=_socket.EndSend(ar);
                    }
                    catch(Exception e)
                    {
                        rlen=0;
                        // Ctrl.printExceptionForIO(e);
                    }

                    _sending=false;

                    if(rlen>0)
                    {

                    }
                    else
                    {
                        preBeClose(5);
                    }

                },null);
            }
            catch(Exception e)
            {
                // Ctrl.printExceptionForIO(e);
                preBeClose(6);
            }
        }

        protected void toBeginReceive(byte[] buf,int off,int len)
        {
            try
            {
                _socket.BeginReceive(buf,off,len,SocketFlags.None,onSocketReceiveAsync,this);
            }
            catch(Exception e)
            {
                // Ctrl.printExceptionForIO(e);
                preBeClose(1);
            }
        }

        /** io线程 */
        private void onSocketReceiveAsync(IAsyncResult ar)
        {
            int len=0;
            bool hasError=false;

            try
            {
                len=_socket.EndReceive(ar);
            }
            catch(Exception e)
            {
                len=0;
                hasError=true;
                // Ctrl.printExceptionForIO(e);
            }

            if(len>0)
            {
                if(!onReceived(len))
                {
                    hasError=true;
                }
            }

            if(!hasError)
            {
                beginReceive();
            }
            else
            {
                if(_socket.Connected)
                {
                    Ctrl.printForIO("preBeClose3 出错时，依然判定连接中");
                }

                preBeClose(3);
            }
        }

        protected void toSend(byte[] buf,int off,int len)
        {
            bool hasError=false;

            if(_socket.Connected)
            {
                try
                {
                    _socket.Send(buf,off,len,SocketFlags.None);
                }
                catch(Exception e)
                {
                    // Ctrl.printExceptionForIO(e);
                    hasError=true;
                }
            }
            else
            {
                hasError=true;
            }

            if(hasError)
            {
                preBeClose(7);
                Thread.Sleep(ShineSetting.ioThreadFrameDelay);
            }
        }

        public void receiveLoopOnce()
        {
            if(!_socket.Connected)
            {
                preBeClose(4);
                return;
            }

            if(!toReceiveLoopOnce())
            {
                preBeClose(8);
            }
        }

        /** 同步读取(IO线程) */
        protected abstract bool toReceiveLoopOnce();

        /** 接收消息(异步)(IO线程) */
        protected abstract bool onReceived(int len);


        protected abstract void doClose();
        /** 异步发送(主线程) */
        public virtual void sendAsyncOnce(byte[] arr,int off,int len)
        {

        }

        /** 同步发送(IO线程) */
        public abstract void toSendStream(BytesWriteStream stream);

        public virtual void onKcpLoop()
        {

        }

        public virtual void kcpSendLoop()
        {

        }

        public void closeTest()
        {
            if(_socket.Connected)
            {
                Ctrl.print("测试断开");
                _socket.Close();
            }
        }

    }
}
