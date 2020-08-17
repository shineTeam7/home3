using System;
using System.Text;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 字节读流c
	/// </summary>
	public class BytesReadStream
	{
		/** 是否使用bitBoolean */
		private bool _useBitBoolean=ShineSetting.bytesUseBitBoolean;

		private byte[] _buf=BytesUtils.EmptyByteArr;

		private int _off=0;

		private int _length=0;

		private int _position=0;

		//read

		/** 读取长度限制(防溢出) */
		private int _readLenLimit=0;
		/** 读取次数 */
		private int _readNum=0;
		/** 读锁,只为加速运算 */
		private bool _readLock=false;

		//c float

		private byte[] _floatTempTrans;

		private IntList _readStack;

		/** 当前位数 */
		private int _booleanBitIndex=-1;
		/** 字节值 */
		private int _booleanBitValue=0;

		/** 数据池 */
		private DataPool _pool;

		public BytesReadStream()
		{

		}

		public BytesReadStream(int size)
		{
			_buf=new byte[size];
		}

		public BytesReadStream(byte[] buf)
		{
			setBuf(buf);
		}

		public BytesReadStream(byte[] buf,int off,int length)
		{
			setBuf(buf,off,length);
		}

		public void setUseBitBoolean(bool value)
		{
			_useBitBoolean=value;
		}

		/// <summary>
		/// 设置操作字节流
		/// </summary>
		public void setBuf(byte[] buf)
		{
			setBuf(buf,0,buf.Length);
		}

		/// <summary>
		/// 设置操作字节流
		/// </summary>
		public void setBuf(byte[] buf,int off,int length)
		{
			if(off>buf.Length)
			{
				off=buf.Length;
			}

			if(length + off>buf.Length)
			{
				length=buf.Length - off;
			}

			clear();

			_buf=buf;
			_off=off;
			_length=length;
		}

		/// <summary>
		/// 获取字节数组
		/// </summary>
		public byte[] getBuf()
		{
			return _buf;
		}

		/// <summary>
		/// 得到字节流位置
		/// </summary>
		public int getPosition()
		{
			return _position;
		}

		/// <summary>
		/// 设置写字节的偏移量
		/// </summary>
		public void setPosition(int pos)
		{
			if(pos<0)
			{
				throwError("位置不能小于0");
				pos=0;
			}

			if(pos>_length)
			{
				tailError();
				pos=_length;
			}

			_position=pos;
		}

		/// <summary>
		/// 得到字节数组的长度
		/// </summary>
		public int length()
		{
			return _length;
		}

		/// <summary>
		/// 设置长度限制(可缩短)
		/// </summary>
		public void setLength(int len)
		{
			if(len + _off>_buf.Length)
			{
				tailError();
			}

			_length=len;
		}

		/// <summary>
		/// 剩余可读数目
		/// </summary>
		public int bytesAvailable()
		{
			return _length - _position;
		}

		/// <summary>
		/// 是否已为空(没有剩余字节)
		/// </summary>
		public bool isEmpty()
		{
			return (_length - _position)==0;
		}

		/** 遇到文件尾 */
		private void tailError()
		{
			int pos=_position;

			setPosition(_length);

			if(_readNum>0)
			{
				_readLock=true;
			}
			else
			{
				throwError("遇到文件尾,pos:"+pos+" length:"+_length);
			}
		}

		public void clearBooleanPos()
		{
			if(!_useBitBoolean)
				return;

			_booleanBitIndex=-1;
			_booleanBitValue=0;
		}

		private IntList getReadStack()
		{
			if(_readStack==null)
				_readStack=new IntList();

			return _readStack;
		}

		/// <summary>
		/// 清空(不清off)
		/// </summary>
		public void clear()
		{
			_position=0;
			_length=0;
			_readNum=0;
			_readLock=false;
			_readLenLimit=0;

			clearBooleanPos();
			if(_readStack!=null)
				_readStack.clear();
		}

		/** 抛错 */
		public void throwError(string str)
		{
			Ctrl.throwError(str);
		}

		/** 抛出类型转发错误 */
		public void throwTypeReadError(Type cls1,Type cls2)
		{
			throwError("读取时，类型升级失败"+cls1.Name+" "+cls2.Name);
		}

		//--read--//

		private bool ensureCanRead(int len)
		{
			if(_readLock)
				return false;

			if(_position + len>_length)
			{
				tailError();
				return false;
			}

			return true;
		}

		/// <summary>
		/// 读出字节组
		/// </summary>
		public bool readByteArr(byte[] bytes,int off,int len)
		{
			if(!ensureCanRead(len))
				return false;

			Buffer.BlockCopy(_buf,_position + _off,bytes,off,len);

			_position+=len;

			return true;
		}

		/** 读出字节组 */
		public byte[] readByteArr(int len)
		{
			if(!ensureCanRead(len))
			{
				return ObjectUtils.EmptyByteArr;
			}

			byte[] re=new byte[len];

			Buffer.BlockCopy(_buf,_position + _off,re,0,len);

			return re;
		}

		/** 读出一个布尔值 */
		public bool readBoolean()
		{
			if(_useBitBoolean)
			{
				//新的
				if(_booleanBitIndex==-1)
				{
					if(!ensureCanRead(1))
						return false;

					_booleanBitValue=_buf[_position++ + _off] & 0xff;


					bool re=(_booleanBitValue & 1)!=0;
					_booleanBitIndex=1;
					return re;
				}
				else
				{
					bool re=(_booleanBitValue & (1<<_booleanBitIndex))!=0;

					if((++_booleanBitIndex)==8)
					{
						_booleanBitIndex=-1;
						_booleanBitValue=0;
					}

					return re;
				}
			}
			else
			{
				if(!ensureCanRead(1))
					return false;

				return _buf[_position++ + _off]!=0;
			}

		}

		/// <summary>
		/// 读出一个字节
		/// </summary>
		public byte readByteB()
		{
			if(!ensureCanRead(1))
				return 0;

			return _buf[_position++ + _off];
		}

		/// <summary>
		/// 读出一个字节
		/// </summary>
		public int readByte()
		{
			if(!ensureCanRead(1))
				return 0;

			return _buf[_position++ + _off];
		}

		/// <summary>
		/// 读出一个无符号字节
		/// </summary>
		public int readUnsignedByte()
		{
			if(!ensureCanRead(1))
				return 0;

			return _buf[_position++ + _off] & 0xff;
		}

		/// <summary>
		/// 读出一个短整型数值
		/// </summary>
		public int readShort()
		{
			if(!ensureCanRead(1))
				return 0;

			int v=_buf[_position + _off];

			int sign=v & 0x80;

			int n=v & 0x7f;

			int re;

			if(n>=0x40)
			{
				++_position;
				re=n & 0x3f;
			}
			else if(n>=0x20)
			{
				re=natureReadShort() & 0x1fff;
			}
			else if(n>=0x10)
			{
				++_position;
				re=natureReadShort();
			}
			else
			{
				throwError("readShort,invalid number:" + n);
				re=0;
			}

			return sign>0 ? -re : re;
		}

		/// <summary>
		/// 原版读short
		/// </summary>
		public int natureReadShort()
		{
			if(!ensureCanRead(2))
				return 0;

			int pos=_position + _off;
			_position+=2;

			byte[] buf=_buf;

			return (short)((buf[pos + 1] & 0xff) | ((buf[pos] & 0xff) << 8));
		}

		/// <summary>
		/// 读出一个无符号的短整型数值
		/// </summary>
		public int readUnsignedShort()
		{
			if(!ensureCanRead(1))
				return 0;

			int n=_buf[_position + _off] & 0xff;

			int re;

			if(n>=0x80)
			{
				++_position;
				re=n & 0x7f;
			}
			else if(n>=0x40)
			{
				re=natureReadUnsignedShort() & 0x3fff;
			}
			else if(n>=0x20)
			{
				++_position;
				re=natureReadUnsignedShort();
			}
			else
			{
				throwError("readShort,invalid number:" + n);
				re=0;
			}

			return re;
		}

		/// <summary>
		/// 原版读ushort
		/// </summary>
		public int natureReadUnsignedShort()
		{
			if(!ensureCanRead(2))
				return 0;

			int pos=_position + _off;
			_position+=2;

			byte[] buf=_buf;

			return (buf[pos + 1] & 0xff) | ((buf[pos] & 0xff) << 8);
		}

		/// <summary>
		/// 读出一个整型数值
		/// </summary>
		public int readInt()
		{
			if(!ensureCanRead(1))
				return 0;

			int v=_buf[_position + _off];

			int sign=v & 0x80;

			int n=v & 0x7f;

			int re;

			if(n>=0x40)
			{
				++_position;
				re=n & 0x3f;
			}
			else if(n>=0x20)
			{
				re=natureReadShort() & 0x1fff;
			}
			else if(n>=0x10)
			{
				re=natureReadInt() & 0x0fffffff;
			}
			else if(n>=8)
			{
				++_position;
				re=natureReadInt();
			}
			else
			{
				throwError("readShort,invalid number:" + n);
				re=0;
			}

			return sign>0 ? -re : re;
		}

		/// <summary>
		/// 原版读int
		/// </summary>
		public int natureReadInt()
		{
			if(!ensureCanRead(4))
				return 0;

			int pos=_position + _off;

			_position+=4;

			byte[] buf=_buf;

			return (buf[pos + 3] & 0xff) | ((buf[pos + 2] & 0xff) << 8) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos] & 0xff) << 24);
		}

		/// <summary>
		/// 读出一个浮点数
		/// </summary>
		public float readFloat()
		{
			if(!ensureCanRead(4))
				return 0f;

			int pos=_position + _off;

			_position+=4;

			byte[] buf=_buf;
			byte[] floatTempTrans=_floatTempTrans;

			if(floatTempTrans==null)
			{
				_floatTempTrans=floatTempTrans=new byte[8];
			}

			floatTempTrans[3]=buf[pos];
			floatTempTrans[2]=buf[pos + 1];
			floatTempTrans[1]=buf[pos + 2];
			floatTempTrans[0]=buf[pos + 3];

			return BitConverter.ToSingle(floatTempTrans,0);
		}

		/// <summary>
		/// 读出一个双精数
		/// </summary>
		public double readDouble()
		{
			if(!ensureCanRead(8))
				return 0.0;

			int pos=_position + _off;

			_position+=8;

			byte[] buf=_buf;
			byte[] floatTempTrans=_floatTempTrans;

			if(floatTempTrans==null)
			{
				_floatTempTrans=floatTempTrans=new byte[8];
			}

			floatTempTrans[7]=buf[pos];
			floatTempTrans[6]=buf[pos + 1];
			floatTempTrans[5]=buf[pos + 2];
			floatTempTrans[4]=buf[pos + 3];
			floatTempTrans[3]=buf[pos + 4];
			floatTempTrans[2]=buf[pos + 5];
			floatTempTrans[1]=buf[pos + 6];
			floatTempTrans[0]=buf[pos + 7];

			return BitConverter.ToDouble(floatTempTrans,0);
		}

		/// <summary>
		/// 读出一个长整型数值
		/// </summary>
		public long readLong()
		{
			if(!ensureCanRead(1))
				return 0L;

			int v=_buf[_position + _off];

			int sign=v & 0x80;

			int n=v & 0x7f;

			long re;

			if(n>=0x40)
			{
				++_position;
				re=n & 0x3f;
			}
			else if(n>=0x20)
			{
				re=natureReadShort() & 0x1fff;
			}
			else if(n>=0x10)
			{
				re=natureReadInt() & 0x0fffffff;
			}
			else if(n>=8)
			{
				++_position;
				re=natureReadInt();
			}
			else if(n>=4)
			{
				++_position;
				re=natureReadLong();
			}
			else
			{
				throwError("readShort,invalid number:" + n);
				re=0;
			}

			return sign>0 ? -re : re;
		}

		/// <summary>
		/// 原版读long
		/// </summary>
		public long natureReadLong()
		{
			if(!ensureCanRead(8))
				return 0L;

			int pos=_position + _off;
			_position+=8;

			byte[] buf=_buf;

			return (buf[pos + 7] & 0xffL) | ((buf[pos + 6] & 0xffL) << 8) | ((buf[pos + 5] & 0xffL) << 16) | ((buf[pos + 4] & 0xffL) << 24) | ((buf[pos + 3] & 0xffL) << 32) | ((buf[pos + 2] & 0xffL) << 40) | ((buf[pos + 1] & 0xffL) << 48) | ((buf[pos] & 0xffL) << 56);
		}

		/// <summary>
		/// 读出指定长的字符串
		/// </summary>
		public string readUTFBytes(int length)
		{
			if(!ensureCanRead(length))
				return "";

			if(length==0)
				return "";

			int pos=_position + _off;

			_position+=length;

			return Encoding.UTF8.GetString(_buf,pos,length);
		}

		/// <summary>
		/// 读出一个字符串，在最前用一UnsignedShort表示字符长度
		/// </summary>
		public string readUTF()
		{
			if(_readLock)
				return "";

			return readUTFBytes(readLen());
		}

		/// <summary>
		/// 读一个长度
		/// </summary>
		public int readLen()
		{
			//这里不走ensure
			if(_readLock)
				return 0;

			if(_position + 1>_length)
			{
				return 0;
			}

			int re;
			int n=_buf[_position + _off] & 0xff;

			if(n>=0x80)
			{
				++_position;
				re= n & 0x7f;
			}
			else if(n>=0x40)
			{
				if(_position + 2>_length)
				{
					return 0;
				}

				re= natureReadUnsignedShort() & 0x3fff;
			}
			else if(n>=0x20)
			{
				if(_position + 4>_length)
				{
					return 0;
				}

				re= natureReadInt() & 0x1fffffff;
			}
			else if(n>=0x10)
			{
				if(_position + 5>_length)
				{
					return 0;
				}

				++_position;

				re= natureReadInt();
			}
			else
			{
				throwError("readLen,invalid number:" + n);
				re=0;
			}

			if(_readLenLimit>0 && re>=_readLenLimit)
			{
				if(ShineSetting.needBytesLenLimitError)
					throwError("readLen,超过长度限制:" + re);
			}

			return re;
		}

		/** 开始读对象 */
		public void startReadObj()
		{
			int len=readLen();
			int pos=_position + len;

			int re=_length;
			_readNum++;
			_length=pos;

			if(_useBitBoolean)
			{
				getReadStack().add3(re,_booleanBitIndex,_booleanBitValue);
				clearBooleanPos();
			}
			else
			{
				getReadStack().add(re);
			}
		}

		/** 结束读对象 */
		public void endReadObj()
		{
			if(_readNum==0)
			{
				throwError("不该出现的");
				return;
			}

			//倒序
			if(_useBitBoolean)
			{
				_booleanBitValue=_readStack.pop();
				_booleanBitIndex=_readStack.pop();
			}

			int len=_readStack.pop();

			setPosition(_length);

			_length=len;

			_readNum--;
			_readLock=false;
		}

		/** 设置读取长度限制 */
		public void setReadLenLimit(int value)
		{
			_readLenLimit=value;
		}

		//--data--//

		/// <summary>
		/// 读出一个非空数据(完整版)
		/// </summary>
		public BaseData readDataFullNotNull()
		{
			if(isEmpty())
				return null;

			startReadObj();

			int dataID=this.readShort();

			BaseData data;

			if(dataID<=0)
			{
				Ctrl.log("不该找不到dataID",dataID);
				data=null;
			}
			else
			{
				data=BytesControl.getDataByID(dataID);

				if(data!=null)
				{
					data.readBytesFullWithoutLen(this);
				}
			}

			endReadObj();

			return data;
		}

		/// <summary>
		/// 读取数据(考虑空)(可继承的)(完整版)
		/// </summary>
		public BaseData readDataFull()
		{
			if(this.readBoolean())
			{
				return readDataFullNotNull();
			}
			else
			{
				return null;
			}
		}

		/// <summary>
		/// 读出一个非空数据(可继承的)(简版)
		/// </summary>
		public BaseData readDataSimpleNotNull()
		{
			if(isEmpty())
				return null;

			int dataID=this.readShort();

			BaseData data;

			if(dataID<=0)
			{
				Ctrl.log("不该找不到dataID",dataID);
				data=null;
			}
			else
			{
				data=BytesControl.getDataByID(dataID);

				if(data!=null)
				{
					data.readBytesSimple(this);
				}
			}

			return data;
		}

		/// <summary>
		/// 读取数据(考虑空)(简版)
		/// </summary>
		public BaseData readDataSimple()
		{
			if(this.readBoolean())
			{
				return readDataSimpleNotNull();
			}
			else
			{
				return null;
			}
		}

		//check

		/// <summary>
		/// 获取某一段的字节hash(short)
		/// </summary>
		public short getHashCheck(int pos,int length)
		{
			return BytesUtils.getHashCheck(_buf,pos + _off,length);
		}

		//uncompress

		/// <summary>
		/// 解压缩(0->length)无视position
		/// </summary>
		public void unCompress()
		{
			byte[] b=BytesUtils.uncompressByteArr(_buf,_off,_length);

			_buf=b;
			_off=0;
			_position=0;
			_length=b.Length;
		}

		public override string ToString()
		{
			return BytesUtils.bytesToString(_buf,_off,_length);
		}

		/** 检查版本 */
		public bool checkVersion(int version)
		{
			if(bytesAvailable()<4)
				return false;

			return natureReadInt()==version;
		}
		
		//unity
		
		public Vector3 readVector3()
		{
			Vector3 re;
			re.x = readFloat();
			re.y = readFloat();
			re.z = readFloat();
			return re;
		}

		/** 创建数据 */
		public BaseData createData(int type)
		{
			if(_pool!=null)
				return _pool.createData(type);

			return BytesControl.getDataByID(type);
		}

		public void setDataPool(DataPool pool)
		{
			_pool=pool;
		}
	}
}