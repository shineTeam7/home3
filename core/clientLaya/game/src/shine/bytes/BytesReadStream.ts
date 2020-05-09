namespace Shine
{
	export class BytesReadStream {
	    public static EmptyByteArr: ArrayBuffer = new ArrayBuffer(0);
	
		private _buf:Laya.Byte;

	    private _off: number = 0;
	    private _length: number = 0;
		private _capacity:number=0;
	    private _position: number = 0;
	
	    /** 读取长度限制(防溢出) */
	    private _readLenLimit: number = 0;
	    /** 读取次数 */
	    private _readNum: number = 0;
	    /** 读锁,只为加速运算 */
	    private _readLock: boolean = false;
	
	    constructor(buf?:ArrayBuffer);
	    constructor(size?:number);
	    constructor(bufArray?:number[]);
	    constructor(args?: any)
	    {
			if(args!=null)
			{
				if(typeof args == "number")
				{
					this._buf=new Laya.Byte(args);
					this._buf.endian=ShineSetting.endian;
					this._capacity=this._buf.length;
				} 
				else if (args instanceof ArrayBuffer)
				{
					this.setBuf(args);
				}
				else if (args instanceof Array)
				{
					this.setBuf2(args);
				}
				else
				{
					Ctrl.throwError("不支持的数据种类",args);
				}
			}
	    }
	
	    /** 设置操作字节流 */
	    public setBuf(buf: ArrayBuffer, off: number = 0, length: number = buf.byteLength): void
	    {
	        if (off > buf.byteLength)
	        {
	            off = buf.byteLength;
	        }

	        if (length + off > buf.byteLength)
	        {
	            length = buf.byteLength - off;
	        }
	
			this._buf=new Laya.Byte();
			this._buf.endian=ShineSetting.endian;
			this._buf.writeArrayBuffer(buf);
			this._buf.pos=off;
	        this._off = off;

	        this._length = length;
			this._capacity=this._buf.length;
	
	        this._position = 0;
	        this._readNum = 0;
	        this._readLock = false;
	        this._readLenLimit = 0;
	    }

		 /** 设置操作字节流 */
	    public setBuf2(buf: number[]): void
	    {
			this._buf=new Laya.Byte();
			this._buf.endian=ShineSetting.endian;

			for(var i:number=0;i<buf.length;++i)
			{
				this._buf.writeByte(buf[i]);
			}

			this._buf.pos=0;
	        this._off = 0;

	        this._length = buf.length;
			this._capacity=this._buf.length;
	
	        this._position = 0;
	        this._readNum = 0;
	        this._readLock = false;
	        this._readLenLimit = 0;
		}

		/** 获取buf */
		public getBuf():ArrayBuffer
		{
			return this._buf.__getBuffer();
		}
	
	    /** 得到字节流位置 */
	    public getPosition(): number
	    {
	        return this._position;
	    }

		public getPositionOff():number
		{
			return this._position+this._off;
		}
	
	    /** 设置写字节的偏移量 */
	    public setPosition(pos:number):void
	    {
	        if (pos < 0)
	        {
	            pos = 0;
	        }
	        if (pos > this._length)
	        {
	            this.tailError();
	            pos = this._length;
	        }
	        this._position = pos;
			this.syncPos();
	    }

		/** 同步位置 */
		private syncPos():void
		{
			this._buf.pos=this._position+this._off;
		}
	
	    /** 得到字节数组的长度 */
	    public length(): number
	    {
	        return this._length;
	    }
	
	    /** 设置长度限制(可缩短) */
	    public setLength(len: number)
	    {
	        if (len + this._off > this._capacity)
	        {
	            this.tailError();
	        }
	
	        this._length = len;
	    }
	
	    /** 剩余可读数目 */
	    public bytesAvailable(): number
	    {
	        return this._length - this._position;
	    }
	
	    /** 是否已为空(没有剩余字节) */
	    public isEmpty(): boolean
	    {
	        return (this._length - this._position) == 0;
	    }
	
	    /** 获取字节 */
	    public getByte(index: number):number
	    {
			// this._buf.pos=this._off+index;
			// var re:number=this._buf.readByte();
			// this.syncPos();
			return this._buf[index+this._off];
	    }
	
	    /** 得到字节缓存的字节数组(副本)(从position 0到length) */
	    public getByteArray(): ArrayBuffer
	    {
			//TODO:确定一下
			return this._buf.buffer;
	    }
	
	    /** 读出字节组 */
	    public readByteArr(len: number): ArrayBuffer
	    {
	        if (!this.ensureCanRead(len)) {
	            return BytesReadStream.EmptyByteArr;
	        }
			
			var p:number=this._position + this._off;
	        var re = this._buf.__getBuffer().slice(p,p+len);
	        this._position += len;
			this.syncPos();

	        return re;
	    }
	
	    /** 读出一个布尔值 */
	    public readBoolean(): boolean
	    {
	        if (!this.ensureCanRead(1)) {
	            return false;
	        }
			this._position++;
			return this._buf.getByte()!=0;
	    }
	
	    /** 读出一个字节 */
	    public readByte(): number
	    {
	        if (!this.ensureCanRead(1))
	        {
	            return 0;
	        }

			this._position++;
			return this._buf.getByte();
	    }
	
	    /** 读出一个短整型数值 */
	    public readShort(): number
	    {
	        if (!this.ensureCanRead(1))
	        {
	            return 0;
	        }

			var v:number=this._buf.getUint8();
	
	        var sign = v & 0x80;
	
	        var n = v & 0x7f;
	
	        var re;
	
	        if(n >= 0x40)
			{
				++this._position;
				re=n & 0x3f;
			}
			else if(n >= 0x20)
			{
				this.syncPos();
				re = this.natureReadShort() & 0x1fff;
			}
			else if(n >= 0x10)
			{
				++this._position;
				re = this.natureReadShort();
			}
			else
			{
				this.syncPos();
				Ctrl.throwError("readShort,invalid number:" + n);
				re = 0;
			}
			
			return sign > 0 ? -re : re;
	    }
	
	    /** 原版读short */
	    public natureReadShort(): number
	    {
	        if (!this.ensureCanRead(2))
	        {
	            return 0;
	        }
			
			this._position += 2;
			return this._buf.getInt16();
	    }
	
	    /** 原版读ushort */
	    public natureReadUnsignedShort(): number
	    {
	        if (!this.ensureCanRead(2))
	        {
	            return 0;
	        }

			this._position += 2;
			return this._buf.getUint16();
	    }
	
	    /** 读出一个整型数值 */
	    public readInt(): number
	    {
	        if (!this.ensureCanRead(1))
	        {
	            return 0;
	        }
	
	        var v:number=this._buf.getUint8();
	
	        var sign = v & 0x80;
	
	        var n = v & 0x7f;
	
	        var re;
	
	        if(n >= 0x40)
			{
				++this._position;
				re = n & 0x3f;
			}
			else if(n >= 0x20)
			{
				this.syncPos();
				re = this.natureReadShort() & 0x1fff;
			}
			else if(n >= 0x10)
			{
				this.syncPos();
				re = this.natureReadInt() & 0x0fffffff;
			}
			else if(n >= 8)
			{
				++this._position;
				re = this.natureReadInt();
			}
			else
			{
				this.syncPos();
				Ctrl.throwError("readShort,invalid number:" + n);
				re=0;
			}
			
	        return sign>0 ? -re : re;
	    }
	
	    /** 原版读int */
	    public natureReadInt(): number
	    {
	        if (!this.ensureCanRead(4))
	        {
	            return 0;
	        }
	
	        this._position += 4;
	        return this._buf.getInt32();
	    }
	
	    /** 读出一个浮点数 */
	    public readFloat(): number
	    {
	        if (this._readLock)
	        {
	            return 0;
	        }
	
	        if (!this.ensureCanRead(4))
	        {
	            return 0;
	        }
	
			this._position += 4;
	        return this._buf.getFloat32();
	    }
	
	    /** 读出一个双精数 */
	    public readDouble(): number
	    {
	        if (this._readLock)
	        {
	            return 0;
	        }
	
	        if (!this.ensureCanRead(8))
	        {
	            return 0;
	        }

			this._position += 8;
	        return this._buf.getFloat64();
	    }
	
	    /** 读出一个长整型数值 */
	    public readLong(): number
	    {
	        if (!this.ensureCanRead(1))
	        {
	            return 0;
	        }
	
	        var v:number=this._buf.getUint8();
			
			let sign=v & 0x80;
			
			let n=v & 0x7f;
			
			let re;
			
			if(n >= 0x40)
			{
				++this._position;
				re = n & 0x3f;
			}
			else if(n >= 0x20)
			{
				this.syncPos();
				re = this.natureReadShort() & 0x1fff;
			}
			else if(n >= 0x10)
			{
				this.syncPos();
				re = this.natureReadInt() & 0x0fffffff;
			}
			else if(n >= 8)
			{
				++this._position;
				re = this.natureReadInt();
			}
			else if(n >= 4)
			{
				++this._position;
				re = this.natureReadLong();
			}
			else
			{
				this.syncPos();
				Ctrl.throwError("readLong,invalid number:" + n);
				re=0;
			}
			
			return sign > 0 ? -re : re;
	    }
	
	    /** 原版读long */
	    public natureReadLong(): number
	    {
	        if (!this.ensureCanRead(8))
	        {
	            return 0;
	        }
	
	        this._position += 8;
			
			var front:number=this._buf.getInt32();

			if(front>=0)
				return front*4294967296 + this._buf.getUint32();
			else
				return front*4294967296 - this._buf.getUint32();

	        // let byte1 = this._data.getInt8(pos + 7) & 0xff;
	        // let byte2 = (this._data.getInt8(pos + 6) & 0xff) << 8;
	        // let byte3 = (this._data.getInt8(pos + 5) & 0xff) << 16;
	        // let byte4 = (this._data.getInt8(pos + 4) & 0xff) << 24;
	        // let byte5 = (this._data.getInt8(pos + 3) & 0xff) << 32;
	        // let byte6 = (this._data.getInt8(pos + 2) & 0xff) << 40;
	        // let byte7 = (this._data.getInt8(pos + 1) & 0xff) << 48;
	        // let byte8 = (this._data.getInt8(pos) & 0xff) << 56;
	        // return byte1 | byte2 | byte3 | byte4 | byte5 | byte6 | byte7 | byte8;
	    }
	
	    /** 读出指定长的字符串 */
	    public readUFTBytes(length: number): string
	    {
	        if (length == 0)
	        {
	            return "";
	        }

	        if (!this.ensureCanRead(length))
	        {
	            return "";
	        }

			this._position+=length;
			var re:string=this._buf.readUTFBytes(length);

			if(ShineSetting.openCheck)
			{
				if(this._buf.pos!=this._position)
				{
					Ctrl.throwError("对不上了");
				}
			}

			return re;
			
	
	        // let str = "";
	        // let c = 0, c2 = 0, c3 = 0;
	
	        // let i=0;
	        // let format = String.fromCharCode;
	        // let max = this._position + length;
	        // let dv = new Uint8Array(this._buf);
	        
	        // while (this._position < max)
	        // {
			// 	c = dv[this._position++];
	        //     if (c < 0x80)
	        //     {
			// 		if (c !=0){
			// 			str += format(c);
			// 		}
	        //     }
	        //     else if (c < 0xE0)
	        //     {
			// 		str += format(((c & 0x3F) << 6)| (dv[this._position++] & 0x7F));
	        //     }
	        //     else if (c < 0xF0)
	        //     {
			// 		c2 = dv[this._position++];
			// 		str += format(((c & 0x1F) << 12)| ((c2 & 0x7F) << 6)| (dv[this._position++] & 0x7F));
	        //     }
	        //     else 
	        //     {
			// 		c2 = dv[this._position++];
			// 		c3 = dv[this._position++];
			// 		str += format(((c & 0x0F) << 18)| ((c2 & 0x7F) << 12)| ((c3 << 6)& 0x7F)| (dv[this._position++] & 0x7F));
			// 	}
			// 	i++;
	        // }
	        
	        // return str;
	    }
	
	    /** 读出一个字符串，在最前用一UnsignedShort表示字符长度 */
	    public readUTF(): string
	    {
	        if (this._readLock)
	        {
	            return "";
	        }
	
	        return this.readUFTBytes(this.readLen());
	    }
	
	    /** 读一个长度 */
	    public readLen(): number
	    {
	        if (this._readLock)
	        {
	            return 0;
	        }
	        if (this._position + 1 > this._length)
	        {
	            return 0;
	        }
	
	        var re;
			var n = this._buf.getUint8();
			
			if(n >= 0x80)
			{
				++this._position;
				re= n & 0x7f;
			}
			else if(n >= 0x40)
			{
				this.syncPos();
				if(this._position + 2 > this._length)
				{
					return 0;
				}
				
				var tt:number=this.natureReadUnsignedShort();

				re = tt & 0x3fff;
			}
			else if(n >= 0x20)
			{
				this.syncPos();
				if(this._position + 4 > this._length)
				{
					return 0;
				}
				
				re = this.natureReadInt() & 0x1fffffff;
			}
			else if(n >= 0x10)
			{
				if(this._position + 5 > this._length)
				{
					this.syncPos();
					return 0;
				}
				
				++this._position;
				re = this.natureReadInt();
			}
			else
			{
				this.syncPos();
				Ctrl.throwError("readLen,invalid number:" + n);
				re=0;
			}
			
			if(this._readLenLimit > 0 && re >= this._readLenLimit)
			{
				Ctrl.throwError("readLen,超过长度限制:" + re);
				re=0;
			}
			
			return re;
	    }
	
	    /** 开始读 */
	    private startRead(end: number): number
	    {
	        let re = this._length;
	        this._readNum++;
	        this._length = end;
	        return re;
	    }
	
	    /** 结束读 */
	    private endRead(len: number): void
	    {
	        if(this._readNum==0)
			{
				// Ctrl.throwError("不该出现的");
				return;
			}
			
			this.setPosition(this._length);
			
			this._length = len;
			
			this._readNum--;
			this._readLock=false;
	    }
	
	    /** 设置读取长度限制 */
	    public setReadLenLimit(value: number):void
	    {
	        this._readLenLimit = value;
	    }
	
	    /** 获取一个读取字节 */
	    public static getReadBytes(stream: BytesReadStream): number
	    {
	        let len = stream.readLen();
	
	        let pos = stream.getPosition() + len;
	
	        return stream.startRead(pos);
	    }
	
	    /** 销毁读取字节 */
	    public static disReadBytes(stream: BytesReadStream, position: number): void
	    {
	        stream.endRead(position);
	    }
	
	    //--data--//
		
		/** 读出一个非空数据(完整版) */
		public readDataFullNotNull(): BaseData
		{
			if(this.isEmpty()) {
	            return null;
	        }
			
			let position = BytesReadStream.getReadBytes(this);
			
			let dataID = this.readShort();
			
			let data;
			
			if(dataID <= 0)
			{
				Ctrl.print("不该找不到dataID");
				data = null;
			}
			else
			{
				data = BytesControl.getDataByID(dataID);
				
				if(data != null)
				{
					data.readBytesFullWithoutLen(this);
				}
			}
			
			BytesReadStream.disReadBytes(this,position);
			
			return data;
		}
		
		/** 读取数据(考虑空)(可继承的)(完整版) */
		public readDataFull(): BaseData
		{
			if(this.readBoolean())
			{
				return this.readDataFullNotNull();
			}
			else
			{
				return null;
			}
		}
		
		/** 读出一个非空数据(可继承的)(简版) */
		public readDataSimpleNotNull(): BaseData
		{
			if(this.isEmpty()) {
	            return null;
	        }
			
			let dataID=this.readShort();
			
			let data;
			
			if(dataID <= 0)
			{
				Ctrl.print("不该找不到dataID");
				data = null;
			}
			else
			{
				data = BytesControl.getDataByID(dataID);
				
				if(data!=null)
				{
					data.readBytesSimple(this);
				}
			}
			
			return data;
		}
		
		/** 读取数据(考虑空)(简版) */
		public readDataSimple(): BaseData
		{
			if(this.readBoolean())
			{
				return this.readDataSimpleNotNull();
			}
			else
			{
				return null;
			}
		}
		
		//check
		
		/** 获取某一段的字节hash(short) */
		public getHashCheck(pos: number, length: number): number
		{
			return BytesUtils.getHashCheck(this._buf.__getBuffer(), pos + this._off, length);
		}
		
		//uncompress
		
		/** 解压缩(0-length)无视position */
		public unCompress(): void
		{
			var inflate = new Zlib.Inflate(new Uint8Array(this._buf.buffer));
			var re:Int8Array=inflate.decompress();

			this._buf.clear();
			this._buf.writeArrayBuffer(re.buffer);
			this._length=this._buf.pos;
			this._position=0;
			this._buf.pos=0;
		}
		
		/** 克隆一个 */
		public clone(): BytesReadStream
		{
			let re = new BytesReadStream(this._length);
			re._buf = this._buf;
			re._off = this._off;
			re._position = this._position;
			re._length =this._length;
			
			return re;
	    }
	    
	    private tailError(): void
	    {
	        this.setPosition(this._length);
	
	        if (this._readNum > 0)
	        {
	            this._readLock = true;
	        }
	        else
	        {
				Ctrl.throwError("遇到文件尾");
	        }
	    }

		/** 清空(不清off) */
	    public clear(): void
	    {
	        this._position = 0;
	        this._length = 0;
	        this._readNum = 0;
	        this._readLock = false;
	        this._readLenLimit = 0;
			
			this._buf.pos=0;
	    }

	    /** 确认可读 */
	    private ensureCanRead(len: number): boolean
	    {
	        if (this._readLock)
	        {
	            return false;
	        }
	        if (this._position + len > this._length)
	        {
	            this.tailError();
	            return false;
	        }
	        return true;
	    }

		/** 检查版本 */
		public checkVersion(version:number):boolean
		{
			if(this.bytesAvailable()<4)
				return false;

			return this.natureReadInt()==version;
		}
	}
}