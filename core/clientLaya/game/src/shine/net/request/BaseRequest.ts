namespace Shine
{
	export class BaseRequest extends BaseData
	{
		/** 是否写过type */
		private _maked:boolean=false;
	
		/** 是否写过type */
		private _writed:boolean=false;
	
		/** 是否需要构造复制(默认复制,不必复制的地方自己设置) */
		private _needMakeCopy:boolean=false;
	
		/** 是否在断开连时不缓存 */
		private _needCacheOnDisConnect:boolean=true;

		/** 是否需要完整写入 */
		private _needFullWrite:boolean=false;

		/** 写入缓存流 */
		private _stream:BytesWriteStream;
	
		/** 是否开启序号检测(默认不开) */
		private _openIndexCheck:boolean=false;
	
		/** 此次的号 */
		public sendMsgIndex:number;

		/** 是否为长消息 */
		private _needMessageLong:boolean=false;
	
		constructor()
		{
			super();
			//客户端不copy
			this._needMakeCopy=false;
		}
	
		/** 设置不在构造时拷贝 */
		protected setDontCopy(): void 
		{
			this._needMakeCopy = false;
		}
	
		/** 设置是否需要在构造时拷贝 */
		protected setNeedMakeCopy(value: boolean): void
		 {
			this._needMakeCopy = value;
		}
	
		/** 是否开启检测 */
		public setOpenCheck(value: boolean): void 
		{
			this._openIndexCheck = value;
		}
	
		/** 构造(制造数据副本) */
		protected make(): void 
		{
			if (this._maked)
				return;
	
			this._maked = true;
	
			if (this._needMakeCopy)
			{
				this.copyData();
			}
		}
	
		/** 把数据拷贝下 */
		protected copyData(): void 
		{
	
		}
	
		/** 执行写入到流 */
		protected doWriteToStream(stream: BytesWriteStream): void 
		{
			//写协议体
			this.doWriteBytesSimple(stream);
		}
	
		/** 执行写入 */
		protected doWriteBytesSimple(stream: BytesWriteStream): void 
		{
			this.beforeWrite();
	
			if(this._needFullWrite)
				this.writeBytesFull(stream);
			else
				this.writeBytesSimple(stream);
		}
	
		/** 写出字节流(直接写的话不必再构造) */
		public write(): void 
		{
			if (this._writed)
				return;
	
			//视为构造完成
			this._maked = true;
	
			this._writed = true;
	
			this._stream = new BytesWriteStream();
	
			this.doWriteToStream(this._stream);
		}
	
		//** 直接写出byte[](sendAbs用) */
		public writeToBytes():ArrayBuffer 
		{
			this.write();
	
			return this._stream.getByteArray();
		}
	
		/** 写到流里 */
		public writeToStream(stream: BytesWriteStream): void 
		{
			if (this._writed) 
			{
				stream.writeBytesStream(this._stream, 0, this._stream.length());
			}
			else 
			{
				this.doWriteToStream(stream);
			}
		}
	
		//send
		
		/** 预备推送(系统用) */
		public preSend(): void 
		{
			//构造一下
			this.make();
		}

		/** 是否需要在断开连接时缓存 */
		protected setDontCache():void
		{
			this._needCacheOnDisConnect=false;
		}

		/** 是否需要在断开连接时缓存 */
		public needCacheOnDisConnect():boolean
		{
			return this._needCacheOnDisConnect;
		}

		/** 设置是否需要完整写入 */
		public setNeedFullWrite(value:boolean):void
		{
			this._needFullWrite=value;
		}
		
		/** 设置为长消息 */
		protected setLongMessage():void
		{
			this._needMessageLong=true;
		}

		public getMessageLimit():number
		{
			return ShineSetting.getMsgBufSize(this._needMessageLong);
		}
	}
}