namespace Shine
{
	export class GameServer extends BaseServer
	{
		/** 当前等待的mid */
		private _currentRequestMid:number=-1;
		/** 所需的resonse组 */
		private _currentResponses:number[]=null;
		/** 记录消息的时间 */
		private _requestRecordTime:number;
		/** 是否显示了delayUI */
		private _isShowDelayUI:boolean=false;

		constructor()
		{
			super();
		}

		public init():void
		{
			super.init();

			if(CommonSetting.clientOpenReconnect)
			{
				//开启重连
				this.getSocket().setOpenReconnect(true);
			}
	
			BytesControl.addIgnoreMessage(SendClientLogRequest.dataID);
			
			this.addResponseMaker(new GameResponseMaker());
			this.addClientRequestBind(new GameRequestBindTool());
			this.addResponseMaker(new CenterResponseMaker());
			this.addClientRequestBind(new CenterRequestBindTool());
		}

		protected onFrame(delay:number):void
		{
			super.onFrame(delay);

			if(this._currentRequestMid>0)
			{
				var time:number=DateControl.getTimeMillis() - this._requestRecordTime;

				if(!this._isShowDelayUI)
				{
					if(time>=Global.showNetDelayMinTime)
					{
						this._isShowDelayUI=true;
						GameC.ui.showNetDelay(true);
					}
				}

				if(time>=Global.showNetDelayMaxTime)
				{
					this.toCancelRequestBind();
				}
			}
		}
	
		public clear():void
		{
			this.toCancelRequestBind();
		}
	
		private clearRequestBind():void
		{
			this._currentRequestMid=-1;
			this._currentResponses=null;
			this._requestRecordTime=0;
			this._isShowDelayUI=false;
		}

		private toCancelRequestBind():void
		{
			if(this._currentRequestMid<=0)
				return;

			if(this._isShowDelayUI)
			{
				this._isShowDelayUI=false;
				GameC.ui.showNetDelay(false);
			}

			this.clearRequestBind();
		}

		protected onConnect()
		{
			GameC.main.connectGameSuccess();
		}
	
		protected onConnectFailed():void
		{
			if(GameC.main.isRunning())
			{
				GameC.main.connectGameFailed();
			}
			else
			{
				Ctrl.print("连接game失败后的无处理");
			}
		}
	
		protected onClose():void
		{
			Ctrl.log("客户端连接断开");

			if(GameC.main.isRunning())
			{
				GameC.main.onGameSocketClosed();
			}
		}

		public checkRequestBind(mid:number):boolean
		{
			var responses:number[]=this._messageBind.get(mid);

			if(responses==null)
				return true;

			//当前有了
			if(this._currentRequestMid>0)
			{
				if(!this._isShowDelayUI)
				{
					this._isShowDelayUI=true;
					GameC.ui.showNetDelay(true);
				}

				return false;
			}
			else
			{
				this._currentRequestMid=mid;
				this._currentResponses=responses;
				this._requestRecordTime=DateControl.getTimeMillis();
				this._isShowDelayUI=false;
				return true;
			}
		}

		public checkResponseUnbind(mid:number):void
		{
			if(this._currentResponses!=null)
			{
				//存在
				if(ObjectUtils.arrayIndexOf(this._currentResponses,mid)!=-1)
				{
					this.toCancelRequestBind();
				}
			}
		}

		/** 取消某消息的绑定(传入request的ID) */
		public cancelRequestBind(mid:number):void
		{
			if(this._currentRequestMid<=0)
				return;

			if(this._currentRequestMid==mid)
			{
				this.toCancelRequestBind();
			}
		}
	}
}