namespace Shine
{
    /** 基础连接内容 */
    export class BaseSocketContent
    {
        private _parent:BaseSocket;

        private _doIndex:number;

        /** layaSocket */
	    private _socket:Laya.Socket;

        /** 接收缓冲 */
	    private _receiveBuffer:LengthBasedFrameBytesBuffer;

        /** 是否已关闭 */
		private _clozed:boolean=false;

        constructor(socket:BaseSocket)
        {
			
            this._parent=socket;

			this._receiveBuffer=new LengthBasedFrameBytesBuffer(ShineSetting.msgBufSize);
	        this._receiveBuffer.setPieceCall(Func.create(this,this.onPiece));
	        this._receiveBuffer.setErrorCall(Func.create(this,this.onError));
        }

        public setDoIndex(value:number):void
		{
			this._doIndex=value;
		}

        private checkIsCurrent():boolean
		{
			return this._parent.getDoIndex()==this._doIndex;
		}

        public connect(host:string,port:number):void
        {
            if(ShineSetting.openCheck && this._socket!=null)
			{
				Ctrl.errorLog("连接时,不该已存在socket");
			}

            this._socket = new Laya.Socket();
			this._socket.on(Laya.Event.OPEN,this,this.onMConnect);
			this._socket.on(Laya.Event.MESSAGE,this,this.onMReceive);
			this._socket.on(Laya.Event.CLOSE,this,this.onMClose);
			this._socket.on(Laya.Event.ERROR,this,this.onMError);

			if(ShineSetting.clientWebSocketUseWSS)
			{
				var url="wss://"+host+":"+port;
				this._socket.connectByUrl(url);
			}
			else
			{
				this._socket.connect(host,port);
			}
        }

        private onMConnect():void
		{
			Ctrl.print("连接成功");
			
			//不在连接中
			if(this._parent.getState()!=BaseSocket.Connecting)
				return;

            this._parent.preConnectSuccessForIO();
		}

		private onMReceive(msg:ArrayBuffer):void
		{
			if(ShineSetting.clientUseBase64)
			{
				msg = Base64Tool.decode(msg);
			}

			this._receiveBuffer.append(msg,0,msg.byteLength);
		}

		private onMClose(e):void
		{
            Ctrl.print("socket被关闭:" + e);

			this.preClosed();
		}

		private onMError(e):void
		{
            if(!this.checkIsCurrent())
				return;

			Ctrl.print("连接出错",e);

			if(this._parent.getState()==BaseSocket.Connecting)
			{
			    this._parent.preConnectFailedForIO();
			}
			else(this._parent.getState()==BaseSocket.Connected)
			{
				this.preClosed();
			}
		}

        /** 连接被 */
        public preClosed():void
        {
            if(!this.checkIsCurrent())
				return;
			
			this._parent.closeForIO(false);
        }

        /** 读取一个 */
	    private onPiece(bytes: ArrayBuffer): void 
	    {
            if(!this.checkIsCurrent())
				return;

			this._parent.onePiece(bytes);
	    }

        /** 读失败(IO线程) */
		private onError(msg:string):void
		{
			Ctrl.print("socket读取失败:" + msg);

			this.preClosed();
		}

        /** 关闭 */
        public close():void
        {
            if(this._clozed)
                return;
            
            this._clozed=true;

            try
            {
                this._socket.close();
            }
            catch(e)
            {

            }
        }

        /** 推字节(IO线程) */
	    public sendOnce(arr:ArrayBuffer):void
	    {
			try
			{
				this._socket.send(arr);
			}
	        catch(e)
			{
				Ctrl.print(e);
			}
	    }
    }
}