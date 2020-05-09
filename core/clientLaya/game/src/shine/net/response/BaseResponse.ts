namespace Shine
{
	export abstract class BaseResponse extends BaseData 
	{
		/** 连接 */
		public socket: BaseSocket;

		/** 是否完整读 */
		private _needFullRead:boolean=false;
	
		/** 是否为长消息 */
		private _needMessageLong:boolean=false;

		public constructor()
		{
			super();
			
			this._dataID=RePingRequest.rePingMid;
		}
	
		/** 是否需要完整读 */
		protected setNeedFullRead(value:boolean):void
		{
			this._needFullRead=value;
		}

		/** 设置为长消息 */
		protected setLongMessage():void
		{
			this._needMessageLong=true;
		}

		/** 是否是长消息 */
		public isLongMessage():boolean
		{
			return this._needMessageLong;
		}

		/** 从流读取 */
		public readFromStream(stream: BytesReadStream): BaseResponse 
		{
			if(this._needFullRead)
				this.readBytesFull(stream);
			else
				this.readBytesSimple(stream);

			return this;
		}
	
		/** 执行入口 */
		public run(): void 
		{
			//统计部分
			this.preExecute();
		}
	
		/** 预备执行*/
		protected preExecute(): void 
		{
			this.execute();
		}
	
		/** 执行*/
		protected execute(): void 
		{
	
		}
	}
}