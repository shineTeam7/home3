namespace Shine
{
	/** socket基类 */
	export class BaseSocket
	{
		/** 关闭的 */
		public static readonly Closed:number=1;
		/** 连接中 */
		public static readonly Connecting:number=2;
		/** 已连接 */
		public static readonly Connected:number=3;

		private static _connectTimeMax:number=5;

		/** 当前状态(主线程写,IO线程读) */
		private _state:number=1;
		/** 执行索引 */		
		private _doIndex:number=0;

	
	    private _host:string;
	    private _port:number;
		
		/** 是否析构了 */
		private _disposed:boolean=false;
		/** 连接计时 */
		private _connectTime:number=0;

		private _secondLastTime:number=0;
	
		/** 连接内容 */
		private _content:BaseSocketContent;

	    //reconnect
	    /** 是否允许断线重连 */
	    private _openReconnect:boolean = false;
	    /** 断线重连是否有效(是否达到缓存数目上限) */
	    private _reconnecting:boolean = false;
		/** 重连剩余时间(秒) */
		private _reconnectLastTime:number=0;

		/** 间隔(3秒) */
		private _reConnectTime:AffairTimeOut;

		private _closeByInitiative:boolean;
	
		/** 接收连接ID */
		private _receiveID:number=-1;
		/** 接收令牌 */
		private _receiveToken:number=-1;

		/** 读流 */
	    private _readStream:BytesReadStream = new BytesReadStream();
	    /** 写流 */
	    private _writeStream:BytesWriteStream = new BytesWriteStream();

	    //ping
	    /** 心跳计时(秒) */
	    private _pingKeepTime:number;
	    /** 心跳时间经过(秒) */
	    private _pingTimePass:number = 0;
	    /** ping间隔秒数 */
	    private _pingIndex:number = 0;
	
	    //try
	    /** 当前尝试次数(-1就是未工作) */
	    private _currentTryCount:number;
	    /** 最多尝试次数 */
	    private _tryCountMax:number = 0;
	
	    /** 连接成功回调*/
	    private _connectCall:Func;
	    /** 连接失败回调*/
	    private _connectFailedCall:Func;
	    /** 连接被关闭回调*/
	    private _closeCall:Func;
	    /** 构造协议回调*/
	    private _createResponseFunc:Func;
	
	    //check
		/** 发送序号 */
	    private _sendMsgIndex:number = 0;
		/** 接收协议序号 */
	    private _receiveMsgIndex:number = 0;

		/** 发送消息缓存队列 */
		private _sendCacheRequestQueue:BaseRequest[];
		/** 断线时发送的序号 */
		private _disConnectLastSendIndex:number=0;

		constructor()
		{
	        this._pingKeepTime = ShineSetting.pingKeepTime;

			this._sendCacheRequestQueue=new Array<BaseRequest>(ShineSetting.requestCacheLen);

			this._reConnectTime=new AffairTimeOut(Func.create(this,this.reconnectTimeOut),3);
		}
	
	    /** 连接回调*/
	    public setConnectCall(func: Func): void  
	    {
	        this._connectCall = func;
	    }
	
	    /* 连接失败回调 */
	    public setConnectFailedCall(func: Func): void  
	    {
	        this._connectFailedCall = func;
	    }
	
	    /** 连接被关闭回调 */
	    public setCloseCall(func: Func): void  
	    {
	        this._closeCall = func;
	    }
	
	    /** 构造协议回调 */
	    public setCreateResponseFunc(func:Func): void  
	    {
	        this._createResponseFunc = func;
	    }
	
	    /** 清空(回归) */
	    public clear(): void 
	    {
			this._writeStream.clear();
	        //序号归零
	        this._sendMsgIndex = 0;
			this._receiveMsgIndex=0;
			this._reconnecting=false;
			this._reconnectLastTime=0;
			this._pingTimePass=0;

			this._receiveID=-1;
			this._receiveToken=-1;
	    }
	
	    /** 每帧 */
	    public onFrame(delay: number): void  
	    {
	        this._secondLastTime += delay;
	
	        if (this._secondLastTime >= 1000) 
	        {
	            this._secondLastTime -= 1000;
	
	            this.onSecond();
	        }

			this.sendLoopOnce();
	    }
	
	    /** 是否开启断线重连 */
	    public setOpenReconnect(value: boolean): void 
	    {
	        this._openReconnect = value;
	    }
	
	    public isOpenReconnect(): boolean  
	    {
	        return this._openReconnect;
	    }
	
	    /** 是否连接着 */
	    public isConnect(): boolean 
	    {
	        return this._state==BaseSocket.Connected;
	    }

		/** 是否连接着 */
	    public isConnecting(): boolean 
	    {
	        return this._state==BaseSocket.Connecting;
	    }

		public getDoIndex():number
		{
			return this._doIndex;
		}

		/** 获取状态 */
		public getState():number
		{
			return this._state;
		}

		/** 设置接收连接信息 */
		public setReceiveInfo(receiveID:number,token:number):void
		{
			//已有
			if(this._receiveID>0)
				return;

			this._receiveID=receiveID;
			this._receiveToken=token;
		}
	
	    /** 连接 */
	    public connect(host:string,port:number): void 
	    {
			//连接尝试2次
			this._tryCountMax=2;
			this.preConnect(host,port);
	    }
	
	    /** 尝试连接(time:秒) */
	    public tryConnect(host:string,port:number):void
	    {
			//连接尝试3次
			this._tryCountMax=0;
			this.preConnect(host,port);
	    }
	
	    /** 重新尝试连接 */
	    public reConnect():void
	    {
	        this.connect(this._host, this._port);
	    }

		private retryConnect():void
		{
			this.toCloseForConnect();
			this.toConnectOnce();
		}

		public preConnectSuccessForIO():void
		{
			if(!this.isConnecting())
				return;

			this._state=BaseSocket.Connected;

			//重连中
			if(this._reconnecting && this._receiveID>0)
			{
				this.refreshPingTime();
				this.sendAbs(SocketReconnectRequest.createSocketReconnect(this._receiveID,this._receiveToken,this._receiveMsgIndex));
				this._reConnectTime.start();
			}
			else
			{
				this.clear();
				this.onConnectSuccess();
			}
		}

		private onConnectSuccess():void
		{
			if(this._connectCall!=null)
				this._connectCall.invoke();
		}

	    private preConnect(host:string,port:number): void  
	    {
			this.close();

	        this._host = host;
	        this._port = port;
	
	        this._currentTryCount = 0;
	
	        this.toConnectOnce();
	    }

	    public closeForIO(isInitiative:boolean,canRec:boolean=true):void
	    {
			if(this._state==BaseSocket.Closed)
				return;

			//正在连接中
			if(this.isConnecting())
			{
				this.stopConnect();
			}

			this._state=BaseSocket.Closed;
			this._doIndex++;//序号加
	
			Ctrl.print("连接断开",isInitiative);

 			if (isInitiative)
	        {
	            this.toFlush();
	        }

			this.onDisconnect();

			//重连中，直接判定失败
			if(!this._reconnecting && this._openReconnect && !isInitiative && canRec && this.canReconnect())
			{
				Ctrl.log("连接断线，进入重连阶段");
				this._reconnecting=true;
				this._disConnectLastSendIndex=this._sendMsgIndex;
				this._reconnectLastTime=ShineSetting.socketReConnectKeepTime;
				this._closeByInitiative=isInitiative;

				this.beginReconnect();
			}
			else
			{
				this._closeByInitiative=isInitiative;
				this.realClose();
			}
	    }

		/** 开始重连(io线程) */
		protected beginReconnect():void
		{
			this._tryCountMax=0;
			this.reConnect();
		}

		/** 实际关闭(IO线程) */
		protected realClose():void
		{
			this._receiveID=-1;
			this._receiveToken=-1;
			this._reConnectTime.stop();

			this._reconnecting=false;
			this._disConnectLastSendIndex=0;

			for(var i:number=this._sendCacheRequestQueue.length-1;i>=0;--i)
			{
				this._sendCacheRequestQueue[i]=null;
			}

			if(!this._closeByInitiative)
			{
				if(this._closeCall!=null)
					this._closeCall.invoke();
			}
		}

		/** 是否可重连 */
		protected canReconnect():boolean
		{
			return this._receiveID>0;
		}

		/** try到时间(io线程) */
		private reconnectTimeOut():void
		{
			this.reconnectFailed();
		}

		/** 重连失败(IO线程) */
		protected reconnectFailed():void
		{
			if(!this._reconnecting)
				return;

			if(this.isConnecting())
			{
				this.stopConnect();
			}

			this.realClose();
		}

		/** 停止连接 */
		protected stopConnect():void
		{

		}

		private onDisconnect():void
		{
			if(this._content!=null)
			{
				this._content.close();
				this._content=null;
			}

			this._reConnectTime.stop();
		}

		private toCloseForConnect():void
		{
			if(this._state==BaseSocket.Closed)
				return;

			this._state=BaseSocket.Closed;

			this._doIndex++;//序号加

			this.onDisconnect();
		}
	
	    /** 推送剩余的消息 */
	    private toFlush():void
	    {
	        this.sendLoopOnce();
	    }
	
	    private doConnectFailed():void
	    {
			if(this._connectFailedCall!=null)
				this._connectFailedCall.invoke();
	    }
	
	    //被关闭
	    public beClose():void
	    {
	        this.closeForIO(false);
	    }
	
	    /** 关闭(不调closeCall) */
	    public close():void
	    {
	        this.closeForIO(true);
	    }

		/** 闪断测试 */
	    public closeTest():void
	    {
	        this.closeForIO(false);
	    }
	
	    /** 刷ping的时间 */
	    public refreshPingTime():void
	    {
	        this._pingTimePass = 0;
	    }

		/** 真析构 */
		public dispose():void
		{
			this.closeAndClear();
			//TODO:后续
		}
	
	    /** 析构 */
	    public closeAndClear():void
	    {
	        this.close();
	        this.clear();
	    }
	
	    private onSecond():void
	    {
	        switch(this._state)
			{
				case BaseSocket.Connected:
				{
					// if(!_socket.Connected)
					// {
					// 	//连接被关闭
					// 	beClose();
					// }

					++this._pingIndex;

					if(this._pingIndex>=ShineSetting.pingTime)
					{
						this._pingIndex=0;

						this.sendPingRequet();
					}

					if(ShineSetting.needPingCut)
					{
						++this._pingTimePass;

						if(this._pingTimePass>ShineSetting.pingKeepTime)
						{
							Ctrl.warnLog("连接超时,强制关闭");

							this.closeForIO(false);
						}
					}
				}
					break;
				case BaseSocket.Connecting:
				{
					//连接超时
					if((++this._connectTime)>=5)
					{
						this.connectTimeOut();
					}
				}
					break;
			}
	    }
	
	    private toConnectOnce():void 
	    {
			this.createNewContent();

	        this._state=BaseSocket.Connecting;
			this._connectTime=0;
	        ++this._currentTryCount;

			Ctrl.print("toConnect一次:",this._host);

			this._content.connect(this._host, this._port);
	    }

		private createNewContent():void
		{
			++this._doIndex;
			this._content=new BaseSocketContent(this);
			this._content.setDoIndex(this._doIndex);
		}

		public preConnectFailedForIO():void
		{
			if(!this.isConnecting())
				return;

			this.toCloseForConnect();

			//超出次数限制
			if(this._tryCountMax>0 && this._currentTryCount>=this._tryCountMax)
			{
				this._currentTryCount=-1;
				this._connectTime=0;

				if(this._connectFailedCall!=null)
					this._connectFailedCall.invoke();
			}
			else
			{
				this.retryConnect();
			}
		}

		private connectTimeOut():void
		{
			if(this._state!=BaseSocket.Connecting)
				return;

			close();

			//超出次数限制
			if(this._tryCountMax>0 && this._currentTryCount>=this._tryCountMax)
			{
				this._currentTryCount=-1;
				this._connectTime=0;
				this.doConnectFailed();
			}
			else
			{
				this.retryConnect();
			}
		}
	
	    private sendLoopOnce():void
	    {
			if(!this.isConnect())
				return;

			//当前没有
			if(this._content==null)
				return;

	        if (this._writeStream.length() > 0)
	        {
				this._content.sendOnce(this._writeStream.getByteArray());
	            this._writeStream.clear();
	        }
	    }
	
	    private toSendOne(request:BaseRequest):void
	    {
			this.toWriteRequestToStream(this._writeStream,request,request.sendMsgIndex);
	    }
	
		/** 实际执行写request到stream */
		private toWriteRequestToStream(stream:BytesWriteStream,request:BaseRequest,index:number):void
		{
			var limit:number=request.getMessageLimit();
			stream.setWriteLenLimit(limit);

			var startPos:number=stream.getPosition();

			var mid:number=request.getDataID();

			//写协议ID(原生写)
			stream.natureWriteUnsignedShort(mid);

			if(!BytesControl.isIgnoreMessage(mid))
			{
				stream.natureWriteShort(index);
			}

			request.writeToStream(stream);

			var endPos:number=stream.getPosition();
			var len:number=endPos-startPos;

			if(len>=limit)
			{
				Ctrl.errorLog("request长度超出上限",len);
			}

			if(ShineSetting.needCustomLengthBasedFrameDecoder)
			{
				stream.insertLenToPos(startPos,len);
			}
			else
			{
				stream.setPosition(startPos);

				stream.insertVoidBytes(4,len);

				stream.natureWriteInt(len);

				stream.setPosition(endPos);
			}
		}

		/** 重连的下一步(主线程) */
		protected doReconnectNext(lastReceiveIndex:number,needReMsg:boolean):void
		{
			//清空
			this._writeStream.clear();

			var dd:number=this.indexD(this._sendMsgIndex,lastReceiveIndex);

			if(dd<0 || dd>=ShineSetting.requestCacheLen)
			{
				Ctrl.warnLog("重连时，消息缓存已失效");

				this.closeForIO(false,false);
				return;
			}

			if(needReMsg)
			{
				this.sendAbs(SocketReconnectSuccessRequest.createSocketReconnectSuccess(this._receiveMsgIndex));
			}

			for(var i=lastReceiveIndex;this.indexD(i,this._sendMsgIndex)<0;i++)
			{
				var request:BaseRequest=this._sendCacheRequestQueue[i & ShineSetting.requestCacheMark];

				if(request==null)
				{
					Ctrl.errorLog("不该找不到消息");
					this.closeForIO(false,false);
					return;
				}
				else
				{
					this.doWriteRequestToStream(request,i);
				}
			}

			this._reconnecting=false;
			this._disConnectLastSendIndex=0;

			//推送一次
			this.toFlush();

			Ctrl.log("socket重连成功:");
		}

	    /** 读取一个 */
	    public onePiece(bytes: ArrayBuffer): void 
	    {
			//更新ping时间
			this.refreshPingTime();
			this.onSocketDataT(bytes);
	    }

		/** 收到一个协议包(IO线程) */
		private onSocketDataT(bytes: ArrayBuffer): void 
		{
			this._readStream.setBuf(bytes);
	
	        var mid: number = this._readStream.natureReadUnsignedShort();
	
			//检测序号
			if(!BytesControl.isIgnoreMessage(mid))
			{
				var receiveIndex:number=this._readStream.natureReadShort();

				var nowIndex:number=this._receiveMsgIndex;

				if(receiveIndex!=nowIndex)
				{
					Ctrl.warnLog("序号检测没对上,"+" nowIndex:"+nowIndex+" receiveIndex:"+receiveIndex+" mid:"+mid);

					//视为被动关闭
					this.closeForIO(false);

					return;
				}

				++this._receiveMsgIndex;
			}
	
	        if (this._createResponseFunc != null) 
	        {
	            var response:BaseResponse = this._createResponseFunc.invoke(mid);
	
	            if (response == null) 
	            {
	                if (ShineSetting.needMessageExistCheck)
	                {
						Ctrl.throwError("未解析mid为" + mid + "的协议");
	                }
	
	                return;
	            }
	
	            response.socket = this;
	
	            if ((response = response.readFromStream(this._readStream)) != null) 
	            {
					this.toRunResponseOne(response);
	            }
	        }
		}
	
	    /** 读失败 */
	    private onError(msg: string): void
	     {
	         Ctrl.print("socket读取失败:" + msg);
	
	        //断开连接
	        close();
	    }
	
	    //心跳回复(io线程)
	    private onRePing(): void
	    {
	        this.refreshPingTime();
	    }
	
	    //执行消息
	    private makeResponse(mid:number): void 
	    {
	
	    }
	
	    /** 执行response们 */
	    private toRunResponseOne(response:BaseResponse):void
	    {
	        if (ShineSetting.needShowMessage && !BytesControl.isIgnoreMessage(response.getDataID()))
	        {
	          Ctrl.debugLog("收消息:","mid:"+response.getDataID(),ShineSetting.needShowMessageDetail ? response.toDataString() : response.getDataClassName());
	        }
	
	        if (ShineSetting.needError)
	        {
	            response.run();
	        }
	        else
	        {
	            try 
	            {
	                response.run();
	            }
	            catch (err)
	            {
	                Ctrl.throwError(err);
	            }
	        }
	    }
	
	    /** 发送ping消息 */
	    private sendPingRequet(): void 
	    {
	        this.sendAbs(PingRequest.createPing());
	    }
	
	    //check
	
	    /** 发送到连接 */
	    public send(request:BaseRequest): void 
	    {
	        if (ShineSetting.needShowMessage) 
	        {
	            var mid:number = request.dataID;
	
				Ctrl.debugLog("发消息:","mid:"+mid,ShineSetting.needShowMessageDetail ? request.toDataString() : request.getDataClassName());
	        }
	
	        //重连中
			if(this.checkRequestNeedSend(request))
			{
				request.preSend();

				this.sendRequestForIO(request);
			}
	    }

		/** 发送request(IO线程) */
		protected sendRequestForIO(request:BaseRequest):void
		{
			if(!this.checkRequestNeedSend(request))
				return;

			var needIndex:boolean=false;
			var index:number=0;

			if(!BytesControl.isIgnoreMessage(request.getDataID()))
			{
				needIndex=true;
				index=this._sendMsgIndex++;

				if(this._sendMsgIndex>32767)
					this._sendMsgIndex-=65536;

				//存起
				this._sendCacheRequestQueue[index & ShineSetting.requestCacheMark]=request;
			}

			if(this._reconnecting)
			{
				if(needIndex)
				{
					//失效
					if(this.indexD(index,this._disConnectLastSendIndex)>=ShineSetting.requestCacheLen)
					{
						this.reconnectFailed();
					}
				}
			}
			else
			{
				this.doWriteRequestToStream(request,index);
			}
		}

		/** 发abs消息 */
		public sendAbs(request:BaseRequest):void
		{
			request.preSend();

			if(this.isConnect())
			{
				this.doWriteRequestToStream(request,0);
			}
		}

		private checkRequestNeedSend(request:BaseRequest):boolean
		{
			//重连中
			if(this._reconnecting)
			{
				//不保留的
				if(!request.needCacheOnDisConnect())
					return false;
			}
			else
			{
				//还未连接
				if(!this.isConnect())
					return false;
			}

			return true;
		}
		
		/** 执行写入流(io线程) */
		private doWriteRequestToStream(request:BaseRequest,index:number):void
		{
			this.toWriteRequestToStream(this._writeStream,request,index);
		}

		/** 返回a1-a2 */
		public indexD(a1:number,a2:number):number
		{
			var re:number=a1 - a2;

			if(re>32767)
				re-=65536;

			if(re<-32768)
				re+=65536;

			return re;
		}

		/** 收到重连成功(IO消息) */
		public onReconnectSuccess(lastReceiveIndex:number):void
		{
			//已不在重连中
			if(!this._reconnecting)
			{
				this.closeForIO(false,false);
				return;
			}

			this.doReconnectNext(lastReceiveIndex,false);
		}
	}
}