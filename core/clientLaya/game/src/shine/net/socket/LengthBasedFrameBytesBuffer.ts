namespace Shine
{
	export class LengthBasedFrameBytesBuffer 
	{
	    private _buf:Laya.Byte;
	
	    private _length: number=0;

		private _capacity:number=0;
	
	    private _position: number = 0;
	
	    /** 头阶段or身体阶段 */
	    private _isHead: boolean = true;
	
	    /** 头类型,也是所需长度(1:byte,2:short,4:int,5:wholeInt) */
	    private _headNeedLen: number = 1;
	
	    private _bodyLen: number=0;
	
	    private _pieceCall:Func;
	
	    private _errorCall:Func;
	
	    constructor(capacity: number)  
	    {
	        this._buf = new Laya.Byte(capacity);
			this._capacity=capacity;
			this._buf.endian=ShineSetting.endian;
	        this._headNeedLen = ShineSetting.needCustomLengthBasedFrameDecoder ? 1 : 2;//short头长
	    }
	
	    /** 添加缓冲 */
	    public append(bs:ArrayBuffer, off: number, length: number): void 
	    {
			var dLen:number;

			var i:number=0;

			while(i<length)
			{
				dLen=Math.min(length-i,this._capacity-this._length);

				this._buf.pos=this._length;
				this._buf.writeArrayBuffer(bs,off+i,dLen);
				this._length+=dLen;
				i+=dLen;
			
				if(!this.toRead())
					return;
			}
	    }
	
	
	    private toRead():boolean
	    {
	        if(!this.read())
				return false;
	
	        if (this._length == this._capacity) 
	        {
	            this.clean();
	
	            //依然没位置
	            if (this._length == this._capacity) 
	            {
	                Ctrl.print("超出预设buffer大小(单包超上限)", this._capacity);

					this.grow(this._capacity<<1);

					// if(ShineSetting.needGrowOnByteLimit)
					// {
					// 	this.grow(this._capacity<<1);
					// }
					// else
					// {
					// 	this.onError("单包超上限,强制关闭连接");
					// 	return false;
					// }
	            }
	        }

			return true;
	    }
	
	    /** gc一下 */
	    private clean():void
	    {
	        if (this._position == 0)
	            return;
	
	        var len:number;
	
	        if ((len = this._length - this._position) > 0)
	        {
				var bb=this._buf.buffer;
				this._buf.pos=0;
				this._buf.writeArrayBuffer(bb,this._position,len);
	        }
	
	        this._position = 0;
			this._buf.pos=0;
			this._length=len;
	    }
	
	    private grow(len:number):void
	    {
	        var cap:number = MathUtils.getPowerOf2(len);
	
	        if (cap > this._buf.length) 
	        {
				this._capacity=cap;
				this._buf.length=cap;
	        }
	    }
	
	    private read():boolean
	    {
	        while (true) 
	        {
	            if (this._isHead) 
	            {
	                //判定位
	
 	                var available:number = this.bytesAvailable();
	
	                if (available >= this._headNeedLen) 
	                {
	                    if (ShineSetting.needCustomLengthBasedFrameDecoder) 
	                    {
	                        //读byte头
	                        if (this._headNeedLen == 1) 
	                        {
								this._buf.pos=this._position;
	                            var n:number = this._buf.getUint8() & 0xff;
	
	                            if (n >= 0x80)
	                            {
	                                ++this._position;
	                                this._bodyLen = n & 0x7f;
	                                this._isHead = false;
	                            }
	                            else if (n >= 0x40) 
	                            {
	                                this._headNeedLen = 2;
	                            }
	                            else if (n >= 0x20) 
	                            {
	                                this._headNeedLen = 4;
	                            }
	                            else if (n >= 0x10)
	                            {
	                                this._headNeedLen = 5;
	                            }
	                            else 
	                            {
	                                this.onError("readLen,invalid number:" + n);
	                                return false;
	                            }
	                        }
	                        else 
	                        {
								this._buf.pos=this._position;

	                            switch (this._headNeedLen) 
	                            {
	                                case 2:
	                                    {
	                                        this._bodyLen = this.natureReadUnsignedShort() & 0x3fff;
	                                    }
	                                    break;
	                                case 4:
	                                    {
	                                        this._bodyLen = this.natureReadInt() & 0x1fffffff;
	                                    }
	                                    break;
	                                case 5:
	                                    {
	                                        ++this._position;
	                                        this._bodyLen = this.natureReadInt();
	                                    }
	                                    break;
	                            }
	
	                            this._isHead = false;
	                        }
	                    }
	                    else 
	                    {
							this._buf.pos=this._position;
	                        this._bodyLen = this.natureReadUnsignedShort();
	
	                        this._isHead = false;
	                    }
	                }
	                else 
	                {
	                    break;
	                }
	            }
	            else 
	            {
	                if (this.bytesAvailable() >= this._bodyLen) 
	                {
						var bb:ArrayBuffer=this._buf.__getBuffer().slice(this._position,this._position+this._bodyLen);
	
	                    this._position += this._bodyLen;
	                    this._isHead = true;
	                    this._bodyLen = 0;
	                    this._headNeedLen = ShineSetting.needCustomLengthBasedFrameDecoder ? 1 : 4;
						
						this._buf.pos=this._position;

	                    this.onePiece(bb);
	                }
	                else 
	                {
	                    break;
	                }
	            }
	        }

			return true;
	    }
	
	    /** 剩余可读数目 */
	    private bytesAvailable():number
	    {
	        return this._length - this._position;
	    }
	
	    private natureReadUnsignedShort():number
	    {
	        var pos:number = this._position;
	
	        this._position += 2;
			return this._buf.getUint16();
	    }
	
	    private natureReadInt():number
	    {
	        var pos:number = this._position;
	
	        this._position += 4;
			return this._buf.getUint32();
	    }
	
	    /// <summary>
	    /// 每片回调
	    /// </summary>
	    public setPieceCall(func:Func):void
	    {
	        this._pieceCall = func;
	    }
	
		/// <summary>
		/// 出错回调
		/// </summary>
		public setErrorCall(func:Func):void
	    {
	        this._errorCall = func;
	    }
	
	    /** 一片ready */
	    private onePiece(bytes: ArrayBuffer): void 
	    {
	        try
	        {
	            if (this._pieceCall != null) 
	            {
	                this._pieceCall.invoke(bytes);
	            }
	        }
	        catch (err)
	        {
	            Ctrl.throwError(err);
	        }
	    }
	
	    /** 出错误 */
	    private onError(msg: string): void 
	    {
	        if (this._errorCall != null) 
	        {
	            this._errorCall.invoke(msg);
	        }
	    }
	}
}