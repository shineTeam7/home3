namespace Shine
{
	export abstract class BytesHttpRequest extends BaseHttpRequest 
	{
		/** -1:io问题,0:成功,>0:逻辑问题 */
		protected _result: number = -1;
	
		/** 是否完整写 */
		private _needFullWrite:boolean=false;

		constructor() 
		{
			super();
			this._method = HttpMethodType.Post;
		}

		protected setNeedFullWrite(value:boolean):void
		{
			this._needFullWrite=value;
		}
	
		protected onError(): void 
		{
			this.onComplete();
		}
	
		protected write(): void 
		{
			this._postStream = new BytesWriteStream();

			this._postStream.setWriteLenLimit(ShineSetting.msgBufSize);
			//协议号
			this._postStream.natureWriteUnsignedShort(this.dataID);
	
			if(this._needFullWrite)
				this.writeBytesFull(this._postStream);
			else
				this.writeBytesSimple(this._postStream);
		}
	
		protected read(): void 
		{
			if (this._resultStream == null || this._resultStream.bytesAvailable() == 0)
				return;
	
			this._result = this._resultStream.readByte();
	
			if (this._resultStream.bytesAvailable() > 0) 
			{
				this.toRead();
			}
		}
	
		protected toRead(): void 
		{
	
		}

		protected readResult(data:BaseData,stream:BytesReadStream):void
		{
			if(this._needFullWrite)
				data.readBytesFull(stream);
			else
				data.readBytesSimple(stream);
		}

		protected doSendSync():void
		{
			Ctrl.throwError("不支持同步");
		}
	}
}