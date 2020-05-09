namespace Shine
{
	export class BytesWriteStream
	{
		public static Integer_MAX_VALUE: number = 0x7fffffff;

		private _buf:Laya.Byte;
		private _length: number = 0;
		private _position: number = 0;
		private _capacity:number=0;
		
		/** 是否可扩容 */
		private _canGrow: boolean = true;
		/** 写入长度限制(防溢出) */
		private _writeLenLimit: number = 0;
		
		constructor(size: number = 8)
		{
			this._buf = new Laya.Byte(MathUtils.getPowerOf2(size));
			this._buf.endian=ShineSetting.endian;
			this._length = 0;
			this._capacity=0;
			this._position = 0;
			this._canGrow = true;
		}

		/** 设置操作字节流 */
		public setBuf(buf: ArrayBuffer): void
		{
			this._canGrow = false;
			
			this._buf = new Laya.Byte(buf);
			this._buf.endian=ShineSetting.endian;
			this._length = buf.byteLength;
			this._capacity = buf.byteLength;
			
			this._position = 0;
			this._writeLenLimit = 0;
		}
		
		/** 获取字节数组 */
		public getBuf(): ArrayBuffer
		{
			return this._buf.__getBuffer();
		}
		
		/** 得到字节流位置 */
		public getPosition(): number
		{
			return this._position;
		}

		/** 同步位置 */
		private syncPos():void
		{
			this._buf.pos=this._position;
		}
		
		/** 设置写字节的偏移量 */
		public setPosition(pos: number): void
		{
			if(pos < 0)
			{
				pos = 0;
			}
			
			if(pos > this._length)
			{
				this.tailError();
				pos = this._length;
			}
			
			this._position = pos;
			this.syncPos();
		}
		
		/** 得到字节数组的长度 */
		public length(): number
		{
			return this._length;
		}
		
		/** 设置长度(只可增) */
		public setLength(len: number): void
		{
			if(len <= this._length)
			{
				return;
			}
			
			//超过了再grow
			if(len > this._capacity)
			{
				this.grow(len);
			}
			
			this._length = len;
		}
		
		/** 遇到文件尾 */
		private tailError(): void
		{
			this.setPosition(this._length);

			// Ctrl.throwError("遇到文件尾");
		}
		
		/** 扩容 */
		private grow(len: number):void
		{
			let cap = MathUtils.getPowerOf2(len);

			if (cap > this._capacity)
			{
				this._capacity=cap;
				//长度
				this._buf.length=this._capacity;
			}
		}
		
		/** 获取字节 */
		public getByte(index: number): number
		{
			this._buf.pos=index;
			var re:number=this._buf.getByte();
			this.syncPos();
			return re;
		}
		
		/** 得到字节缓存的字节数组(副本)(从position 0到length) */
		public getByteArray(): ArrayBuffer
		{
			return this._buf.__getBuffer().slice(0, this._length);
		}
		
		/** 清空(不清off) */
		public clear(): void
		{
			this._position = 0;
			this._length = 0;
			this._writeLenLimit = 0;
			// this._buf.clear();
			this._buf.pos=0;
		}
		
		//--write--//
		
		/** 写入字节组 */
		public writeByteArr(bbs: ArrayBuffer, off: number = 0, length: number = bbs.byteLength): void
		{
			if(!this.ensureCanWrite(length))
			{
				return;
			}
			
			this._position += length;
			this._buf.writeArrayBuffer(bbs,off,length);
		}
		
		/** 在当前position插入空字节组(后面的后移,position不变) */
		public insertVoidBytes(num: number, length: number): void
		{
			if(!this.ensureCanWrite(num + length))
			{
				return;
			}

			var pp:number=this._position;
			var bb:ArrayBuffer=this._buf.buffer;
			
			this._buf.pos=this._position+num;
			this._buf.writeArrayBuffer(bb,pp,length);
			this.syncPos();
		}
		
		/** 写入一个布尔值 */
		public writeBoolean(value: boolean): void
		{
			if(!this.ensureCanWrite(1))
			{
				return;
			}
			
			this._buf.writeByte(value ? 1 : 0);
			this._position++;
		}
		
		/** 写入一个字节 */
		public writeByte(value: number): void
		{
			if(!this.ensureCanWrite(1))
			{
				return;
			}

			this._buf.writeByte(value);
			this._position++;
		}
		
		/** 写入一个无符号的短整型数值 */
		public writeUnsignedByte(value: number): void
		{
			if(!this.ensureCanWrite(1))
			{
				return;
			}

			this._buf.writeByte(value);
			this._position++;
		}
		
		/** 写入一个短整型数值 */
		public writeShort(value: number): void
		{
			var sign = value < 0 ? 0x80 : 0;
			var v = value >= 0 ? value : -value;
			
			if(v < 0x40)
			{
				this.writeUnsignedByte((sign | 0x40) | v);
			}
			else if(v < 0x2000)
			{
				this.natureWriteShort((sign << 8 | 0x2000) | v);
			}
			else
			{
				this.writeUnsignedByte(sign | 0x10);
				this.natureWriteShort(v);
			}
		}
		
		/** 原版写short */
		public natureWriteShort(value: number): void
		{
			if(!this.ensureCanWrite(2))
			{
				return;
			}

			this._buf.writeInt16(value);
			this._position+=2;
		}
		
		/** 写入一个无符号的短整型数值 */
		public writeUnsignedShort(value: number): void
		{
			if(value < 0x80)
			{
				this.writeUnsignedByte(value | 0x80);
			}
			else if(value < 0x4000)
			{
				this.natureWriteUnsignedShort(value | 0x4000);
			}
			else
			{
				this.writeUnsignedByte(0x20);
				this.natureWriteUnsignedShort(value);
			}
		}
		
		/** 原版写ushort */
		public natureWriteUnsignedShort(value: number): void
		{
			if(!this.ensureCanWrite(2))
			{
				return;
			}

			this._buf.writeUint16(value);
			this._position+=2;
		}
		
		/** 写入一个整型数值 */
		public writeInt(value: number): void
		{
			let sign: number = value < 0 ? 0x80 : 0;
			let v: number = value >= 0 ? value : -value;
			
			if( v< 0x40)
			{
				this.writeUnsignedByte((sign | 0x40) | v);
			}
			else if(v < 0x2000)
			{
				this.natureWriteShort((sign << 8 | 0x2000) | v);
			}
			else if(v < 0x10000000)
			{
				this.natureWriteInt((sign << 24 | 0x10000000) | v);
			}
			else
			{
				this.writeUnsignedByte(sign | 8);
				this.natureWriteInt(v);
			}
		}
		
		/** 原版写int */
		public natureWriteInt(value: number): void
		{
			if(!this.ensureCanWrite(4))
			{
				return;
			}
			
			this._buf.writeInt32(value);
			this._position+=4;
		}
		
		/** 写入一个浮点值 */
		public writeFloat(value: number): void
		{
			if (!this.ensureCanWrite(4))
			{
				return;
			}

			this._buf.writeFloat32(value);
			this._position+=4;
		}
		
		/** 写一个双精数 */
		public writeDouble(value: number): void
		{
			if (!this.ensureCanWrite(8))
			{
				return;
			}

			this._buf.writeFloat64(value);
			this._position+=8;
		}
		
		/** 写入一个长整型数值 */
		public writeLong(value: number): void
		{
			let sign = value < 0 ? 0x80 : 0;
			let v = value >= 0 ? value : -value;
			
			if(v < 0x40)
			{
				this.writeUnsignedByte((sign | 0x40) | v);
			}
			else if(v < 0x2000)
			{
				this.natureWriteShort((sign << 8 | 0x2000) | v);
			}
			else if(v < 0x10000000)
			{
				this.natureWriteInt((sign << 24 | 0x10000000) | v);
			}
			else if(v < BytesWriteStream.Integer_MAX_VALUE)
			{
				this.writeUnsignedByte(sign | 8);
				this.natureWriteInt(v);
			}
			else
			{
				this.writeUnsignedByte(sign | 4);
				this.natureWriteLong(v);
			}
		}
		
		/** 原版写long */
		public natureWriteLong(value: number): void
		{
			if(!this.ensureCanWrite(8))
			{
				return;
			}

			

			this._buf.writeInt32(value/4294967296);
			this._buf.writeUint32(value % 4294967296);
			// this._buf.writeUint32(value & 0xffffffff);
			this._position+=8;
			
			// this._data.setInt8(this._position++, value >>> 56);
			// this._data.setInt8(this._position++, value >>> 48);
			// this._data.setInt8(this._position++, value >>> 40);
			// this._data.setInt8(this._position++, value >>> 32);
			// this._data.setInt8(this._position++, value >>> 24);
			// this._data.setInt8(this._position++, value >>> 16);
			// this._data.setInt8(this._position++, value >>> 8);
			// this._data.setInt8(this._position++, value);
		}
		
		/** 写入一字符串 */
		public writeUTFBytes(str: string): void
		{
			if(str == null)
			{
				str = "";
			}
			
			this._buf.writeUTFBytes(str);

			var strBuf:ArrayBuffer = BytesUtils.getUTFBytes(str);

			this.writeByteArr(strBuf);
		}
		
		/** 写入一字符串，前面加上UnsignedShort长度前缀 */
		public writeUTF(str: string): void
		{
			if(str == null)
			{
				str = "";
			}

			var strBuf:ArrayBuffer = BytesUtils.getUTFBytes(str);

			this.writeLen(strBuf.byteLength);

			this.writeByteArr(strBuf);
		}
		
		/** 写一个长度(只处理正整数) */
		public writeLen(value: number): void
		{
			if(this._writeLenLimit > 0 && value >= this._writeLenLimit)
			{
				Ctrl.throwError("writeLen,超过长度限制:" + value);
			}
			
			if(value < 0)
			{
				this.writeByte(0);
			}
			else if(value < 0x80)
			{
				this.writeUnsignedByte(value | 0x80);
			}
			else if(value < 0x4000)
			{
				this.natureWriteShort(value | 0x4000);
			}
			else if(value < 0x20000000)
			{
				this.natureWriteInt(value | 0x20000000);
			}
			else
			{
				this.writeByte(0x10);
				this.natureWriteInt(value);
			}
		}
		
		/** 设置写入长度限制 */
		public setWriteLenLimit(value: number): void
		{
			this._writeLenLimit = value;
		}
		
		//--len--//
		
		/** 获取长度尺寸 */
		public static getLenSize(value: number): number
		{
			if(value < 0)
			{
				Ctrl.throwError("长度不能小于0");
				return 1;
			}
			else if(value<0x80)
			{
				return 1;
			}
			else if(value<0x4000)
			{
				return 2;
			}
			else if(value<0x20000000)
			{
				return 4;
			}
			else
			{
				return 5;
			}
		}
		
		/** 初始化读取字节(返回当前位置) */
		public static getWriteBytes(stream: BytesWriteStream): number
		{
			return stream.getPosition();
		}
		
		/** 回归读取字节 */
		public static disWriteBytes(stream: BytesWriteStream, position: number): void
		{
			let len = stream.getPosition() - position;
			
			let size = this.getLenSize(len);

			stream.setPosition(position);

			stream.insertVoidBytes(size, len);

			stream.writeLen(len);

			stream.setPosition(position + len + size);
		}
		
		//--data--//
		
		/** 写入一个非空数据(可继承的)(完整版) */
		public writeDataFullNotNull(data: BaseData): void
		{
			let position = BytesWriteStream.getWriteBytes(this);
			
			let dataID = data.dataID;
			
			if(dataID > 0)
			{
				this.writeShort(dataID);
				
				data.writeBytesFullWithoutLen(this);
			}
			else
			{
				Ctrl.print("不该找不到dataID");
				this.writeShort(-1);
			}
			
			BytesWriteStream.disWriteBytes(this, position);
		}
		
		/** 写入数据(考虑空)(可继承的)(完整版) */
		public writeDataFull(data: BaseData): void
		{
			if(data!=null)
			{
				this.writeBoolean(true);
				
				this.writeDataFullNotNull(data);
			}
			else
			{
				this.writeBoolean(false);
			}
		}
		
		/** 写入一个非空数据(可继承的)(简版) */
		public writeDataSimpleNotNull(data: BaseData): void
		{
			let dataID = data.dataID;
			
			if(dataID > 0)
			{
				this.writeShort(dataID);
				
				data.writeBytesSimple(this);
			}
			else
			{
				Ctrl.print("不该找不到dataID");
				this.writeShort(-1);
			}
		}
		
		/** 写入数据(考虑空)(简版) */
		public writeDataSimple(data: BaseData): void
		{
			if(data != null)
			{
				this.writeBoolean(true);
				
				this.writeDataSimpleNotNull(data);
			}
			else
			{
				this.writeBoolean(false);
			}
		}
		
		//--check--//
		
		/** 获取某一段的字节hash(short) */
		public getHashCheck(pos: number, length: number): number
		{
			return BytesUtils.getHashCheck(this._buf.__getBuffer(), pos, length);
		}
		
		
		//--stream--//
		
		/** 写读流 */
		public writeReadStream(stream: BytesReadStream): void
		{
			this.writeByteArr(stream.getBuf(), stream.getPositionOff(), stream.bytesAvailable());
		}
		
		/** 将流写入自身 */
		public writeBytesStream(stream: BytesWriteStream, pos: number = 0, length: number = stream.length()): void
		{
			if(stream.length()<pos + length)
			{
				this.tailError();
				return;
			}
			
			this.writeByteArr(stream._buf.__getBuffer(), pos, length);
		}
		
		//compress
		
		/** 压缩(0-length)(无视postion) */
		public compress(): void
		{
			// TODO
			// byte[] b=BytesUtils.compressByteArr(_buf,0,_length);
			
			// this._buf=b;
			// this._position=0;
			// this._length=b.length;
		}
		
		//ex
		
		/** 写入netty字节组 */
		public writeByteBuf(buf: ArrayBuffer): void
		{
			this.writeByteArr(buf,0,buf.byteLength);
		}
		
		/** 确认可写 */
		private ensureCanWrite(len: number): boolean
		{
			if(this._position + len > this._length)
			{
				if(this._canGrow)
				{
					this.setLength(this._position + len);
				}
				else
				{
					this.tailError();
					return false;
				}
			}
			
			return true;
		}

		/** 写版本号 */
		public writeVersion(v:number):void
		{
			this.natureWriteInt(v);
		}

		/** 在指定位置插入一个len,之后设置为 当前位置+size (若默认值，则为当前Position与pos之差) */
		public insertLenToPos(pos:number,len:number=-1):void
		{
			if(len==-1)
			{
				len=this.getPosition()-pos;
			}

			var cp:number=this.getPosition();

			this.setPosition(pos);

			var size:number=BytesWriteStream.getLenSize(len);

			this.insertVoidBytes(size,len);

			this.writeLen(len);

			this.setPosition(cp+size);
		}
	}
}