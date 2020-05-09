namespace Shine
{
	export class BaseData
	{
		/** 数据类型ID */
		protected _dataID: number = -1;
		/** 是否是空读出来的(BytesStream为0继续读的) */
		private _isEmptyRead: boolean = false;

		constructor()
		{

		}

		public get dataID(): number
		{
			return this._dataID;
		}

		public getDataID(): number
		{
			return this._dataID;
		}

		/** 是否相同类型 */
		public isSameType(data: BaseData): boolean
		{
			return this._dataID == data._dataID;
		}

		/** 是否空读出 */
		public isEmptyRead(): boolean
		{
			return this._isEmptyRead;
		}

		/** 读取字节流(完整版,为DB) */
		public readBytesFull(stream: BytesReadStream): void
		{
			//空读标记
			this._isEmptyRead = stream.isEmpty();
			
			let position = BytesReadStream.getReadBytes(stream);

			this.toReadBytesFull(stream);

			BytesReadStream.disReadBytes(stream, position);

			this.afterRead();
		}
		
		/** 写入字节流(完整版,为DB) */
		public writeBytesFull(stream: BytesWriteStream): void
		{
			this.beforeWrite();
			
			let position = BytesWriteStream.getWriteBytes(stream);

			this.toWriteBytesFull(stream);

			BytesWriteStream.disWriteBytes(stream, position);
		}
		
		/** 读取字节流(不加长度标记)(完整版,为DB) */
		public readBytesFullWithoutLen(stream: BytesReadStream): void
		{
			this.toReadBytesFull(stream);
			
			this.afterRead();
		}
		
		/** 写入字节流不加长度标记)(完整版,为DB) */
		public writeBytesFullWithoutLen(stream: BytesWriteStream): void
		{
			this.beforeWrite();
			
			this.toWriteBytesFull(stream);
		}
		
		/** 读取字节流(简版,为通信) */
		public readBytesSimple(stream: BytesReadStream): void
		{
			this.toReadBytesSimple(stream);
			
			this.afterRead();
		}
		
		/** 写入字节流(简版,为通信) */
		public writeBytesSimple(stream: BytesWriteStream): void
		{
			this.beforeWrite();
			
			this.toWriteBytesSimple(stream);
		}
		
		/** 实际读取字节流(完整版,为DB) */
		protected toReadBytesFull(stream: BytesReadStream): void
		{
			
		}
		
		/** 实际写入字节流(完整版,为DB) */
		protected toWriteBytesFull(stream: BytesWriteStream): void
		{
			
		}
		
		/** 实际读取字节流(简版,为通信) */
		protected toReadBytesSimple(stream: BytesReadStream): void
		{
			
		}
		
		/** 实际写入字节流(简版,为通信) */
		protected toWriteBytesSimple(stream: BytesWriteStream): void
		{
			
		}
		
		/** 复制(深拷)(从目标往自己身上赋值) */
		public copy(data: BaseData): void
		{
			data.beforeWrite();
			
			this.toCopy(data);
			
			this.afterRead();
		}
		
		/** 克隆数据 */
		public clone():BaseData
		{
			var re:BaseData=BytesControl.getDataByID(this._dataID);

			re.copy(this);

			return re;
		}
		
		/** 复制 */
		protected toCopy(data: BaseData): void
		{
			
		}
		
		/** 复制(潜拷)(从目标往自己身上赋值) */
		public shadowCopy(data: BaseData): void
		{
			data.beforeWrite();
			
			this.toShadowCopy(data);
			
			this.afterRead();
		}
		
		/** 复制 */
		protected toShadowCopy(data: BaseData): void
		{
			
		}
		
		/** 是否数据一致 */
		public dataEquals(data: BaseData): boolean
		{
			if(data == null)
				return false;
			
			if(data._dataID != this._dataID)
				return false;
			
			return this.toDataEquals(data);
		}
		
		/** 是否数据一致 */
		protected  toDataEquals(data: BaseData): boolean
		{
			return true;
		}
		
		public toDataString(): string
		{
			let writer = new DataWriter();

			this.writeDataString(writer);

			return writer.releaseStr();
		}
		
		/** 获取数据类名 */
		public getDataClassName(): string
		{
			return "";
		}
		
		public writeDataString(writer: DataWriter): void
		{
			writer.writeCustom(this.getDataClassName());
			writer.writeEnter();

			writer.writeLeftBrace();
			
			this.toWriteDataString(writer);

			writer.writeRightBrace();
		}
		
		protected toWriteDataString(writer: DataWriter): void
		{
		
		}
		
		/** 初始化初值 */
		public initDefault(): void
		{
		
		}
		
		//接口
		
		/** 写前 */
		protected beforeWrite(): void
		{
			
		}
		
		/** 读后 */
		protected afterRead(): void
		{
			
		}
		
		/** 清空 */
		public clear(): void
		{
		
		}
		
		/** 初始化列表数据(ListData用) */
		public initListData(): void
		{
		
		}
	}
}