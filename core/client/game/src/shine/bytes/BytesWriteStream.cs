using System;
using System.Text;
using UnityEngine;

namespace ShineEngine
{
	/// <summary>
	/// 字节写流
	/// </summary>
	public class BytesWriteStream:PoolObject
	{
		/** 是否使用bitBoolean */
		private bool _useBitBoolean=ShineSetting.bytesUseBitBoolean;

		private byte[] _buf;

		private int _length=0;

		private int _position=0;

		/** 是否可扩容 */
		private bool _canGrow=true;
		/** 写入长度限制(防溢出) */
		private int _writeLenLimit=0;

		private IntList _writeStack;

		private int _booleanBufPos=-1;
		private int _booleanBitIndex=0;

		public BytesWriteStream():this(8)
		{

		}

		public BytesWriteStream(int size)
		{
			_buf=new byte[MathUtils.getPowerOf2(size)];
			_length=0;
			_position=0;
			_canGrow=true;
		}

		public BytesWriteStream(byte[] buf)
		{
			setBuf(buf);

			_canGrow=false;
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
			_buf=buf;
			_length=buf.Length;

			_position=0;
			_writeLenLimit=0;
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
		/// 设置长度
		/// </summary>
		/// <param name="len"></param>
		public void setLength(int len)
		{
			if(len<=_length)
				return;

			if(len>=_buf.Length)
			{
				grow(len);
			}

			_length=len;
		}

		/// <summary>
		/// 遇到文件尾
		/// </summary>
		private void tailError()
		{
			int pos=_position;

			setPosition(_length);

			throwError("遇到文件尾,pos:"+pos+" length:"+_length);
		}

		/** 扩容 */
		private void grow(int len)
		{
			int cap=BytesUtils.getCapacitySize(len);

			if(cap>_buf.Length)
			{
				byte[] bs=new byte[cap];

				Buffer.BlockCopy(_buf,0,bs,0,_buf.Length);

				_buf=bs;
			}
		}

		/// <summary>
		/// 获取字节
		/// </summary>
		public byte getByte(int index)
		{
			return _buf[index];
		}

		/// <summary>
		/// 得到字节缓存的字节数组(副本)(从position 0到length)
		/// </summary>
		/// <returns></returns>
		public byte[] getByteArray()
		{
			byte[] re=new byte[_length];
			Buffer.BlockCopy(_buf,0,re,0,_length);

			return re;
		}

		/// <summary>
		/// 清空(不清off)
		/// </summary>
		public override void clear()
		{
			_position=0;
			_length=0;
			_writeLenLimit=0;

			clearBooleanPos();
			if(_writeStack!=null)
				_writeStack.clear();
		}

		/** 抛错 */
		public void throwError(string str)
		{
			Ctrl.throwError(str);
		}

		public void clearBooleanPos()
		{
			if(!_useBitBoolean)
				return;
			
			_booleanBufPos=-1;
			_booleanBitIndex=0;
		}

		private IntList getWriteStack()
		{
			if(_writeStack==null)
				_writeStack=new IntList();

			return _writeStack;
		}

		//--write--//

		/** 确认可写 */
		private bool ensureCanWrite(int len)
		{
			if(_position + len>_length)
			{
				if(_canGrow)
				{
					setLength(_position + len);
				}
				else
				{
					tailError();
					return false;
				}
			}

			return true;
		}

		/// <summary>
		/// 写入字节组
		/// </summary>
		public void writeByteArr(byte[] bbs)
		{
			writeByteArr(bbs,0,bbs.Length);
		}

		/// <summary>
		/// 写入字节组
		/// </summary>
		public void writeByteArr(byte[] bbs,int off,int length)
		{
			if(!ensureCanWrite(length))
				return;

			Buffer.BlockCopy(bbs,off,_buf,_position,length);

			_position+=length;
		}

		/// <summary>
		/// 在当前position插入空字节组(后面的后移,position不变)
		/// </summary>
		public void insertVoidBytes(int num,int length)
		{
			if(!ensureCanWrite(num + length))
				return;

			int d=_position;

			Buffer.BlockCopy(_buf,d,_buf,d + num,length);
		}

		/// <summary>
		/// 写入一个布尔值
		/// </summary>
		public void writeBoolean(bool value)
		{
			if(_useBitBoolean)
			{
				//新的
				if(_booleanBufPos==-1)
				{
					if(!ensureCanWrite(1))
						return;

					_booleanBufPos=_position;
					_buf[_position++]=(byte)(value ? 1 : 0);
					_booleanBitIndex=1;
				}
				else
				{
					if(value)
					{
						_buf[_booleanBufPos] |= (byte)(1<<_booleanBitIndex);
					}

					if((++_booleanBitIndex)==8)
					{
						_booleanBufPos=-1;
						_booleanBitIndex=0;
					}
				}
			}
			else
			{
				if(!ensureCanWrite(1))
					return;

				_buf[_position++]=(byte)(value ? 1 : 0);
			}
		}

		/// <summary>
		/// 写入一个字节
		/// </summary>
		public void writeByte(int value)
		{
			if(!ensureCanWrite(1))
				return;

			_buf[_position++]=(byte)value;
		}

		/// <summary>
		/// 写入一个无符号的短整型数值
		/// </summary>
		public void writeUnsignedByte(int value)
		{
			if(!ensureCanWrite(1))
				return;

			_buf[_position++]=(byte)value;
		}

		/// <summary>
		/// 写入一个短整型数值
		/// </summary>
		public void writeShort(int value)
		{
			int sign=value<0 ? 0x80 : 0;
			int v=value>=0 ? value : -value;

			if(v<0x40)
			{
				writeUnsignedByte((sign | 0x40) | v);
			}
			else if(v<0x2000)
			{
				natureWriteShort((sign << 8 | 0x2000) | v);
			}
			else
			{
				writeUnsignedByte(sign | 0x10);
				natureWriteShort(v);
			}
		}

		/// <summary>
		/// 原版写short
		/// </summary>
		public void natureWriteShort(int value)
		{
			if(!ensureCanWrite(2))
				return;

			int pos=_position;

			byte[] buf=_buf;

			buf[pos]=(byte)(value >> 8);
			buf[pos + 1]=(byte)(value);

			_position+=2;
		}

		/// <summary>
		/// 写入一个无符号的短整型数值
		/// </summary>
		public void writeUnsignedShort(int value)
		{
			if(value<0x80)
			{
				writeUnsignedByte(value | 0x80);
			}
			else if(value<0x4000)
			{
				natureWriteUnsignedShort(value | 0x4000);
			}
			else
			{
				writeUnsignedByte(0x20);
				natureWriteUnsignedShort(value);
			}
		}

		/// <summary>
		/// 原版写ushort
		/// </summary>
		public void natureWriteUnsignedShort(int value)
		{
			if(!ensureCanWrite(2))
				return;

			int pos=_position;

			byte[] buf=_buf;

			buf[pos]=(byte)(value >> 8);
			buf[pos + 1]=(byte)(value);

			_position+=2;
		}

		/// <summary>
		/// 写入一个整型数值
		/// </summary>
		public void writeInt(int value)
		{
			int sign=value<0 ? 0x80 : 0;
			int v=value>=0 ? value : -value;

			if(v<0x40)
			{
				writeUnsignedByte((sign | 0x40) | v);
			}
			else if(v<0x2000)
			{
				natureWriteShort((sign << 8 | 0x2000) | v);
			}
			else if(v<0x10000000)
			{
				natureWriteInt((sign << 24 | 0x10000000) | v);
			}
			else
			{
				writeUnsignedByte(sign | 8);
				natureWriteInt(v);
			}
		}

		/// <summary>
		/// 原版写int
		/// </summary>
		public void natureWriteInt(int value)
		{
			if(!ensureCanWrite(4))
				return;

			int pos=_position;
			_position+=4;

			byte[] buf=_buf;

			buf[pos]=(byte)(value >> 24);
			buf[pos + 1]=(byte)(value >> 16);
			buf[pos + 2]=(byte)(value >> 8);
			buf[pos + 3]=(byte)(value);
		}

		/// <summary>
		/// 写入一个浮点数
		/// </summary>
		public void writeFloat(float value)
		{
			if(!ensureCanWrite(4))
				return;

			byte[] bb=BitConverter.GetBytes(value);

			int pos=_position;
			_position+=4;

			byte[] buf=_buf;

			buf[pos]=bb[3];
			buf[pos + 1]=bb[2];
			buf[pos + 2]=bb[1];
			buf[pos + 3]=bb[0];
		}

		/// <summary>
		/// 写入一个双精数
		/// </summary>
		public void writeDouble(double value)
		{
			if(!ensureCanWrite(8))
				return;

			byte[] bb=BitConverter.GetBytes(value);

			int pos=_position;
			_position+=8;

			byte[] buf=_buf;

			buf[pos]=bb[7];
			buf[pos + 1]=bb[6];
			buf[pos + 2]=bb[5];
			buf[pos + 3]=bb[4];
			buf[pos + 4]=bb[3];
			buf[pos + 5]=bb[2];
			buf[pos + 6]=bb[1];
			buf[pos + 7]=bb[0];
		}

		/// <summary>
		/// 写入一个长整型数值
		/// </summary>
		/// <param name="value"></param>
		public void writeLong(long value)
		{
			int sign=value<0 ? 0x80 : 0;
			long v=value>=0 ? value : -value;

			if(v<0x40)
			{
				writeUnsignedByte((sign | 0x40) | (int)v);
			}
			else if(v<0x2000)
			{
				natureWriteShort((sign << 8 | 0x2000) | (int)v);
			}
			else if(v<0x10000000)
			{
				natureWriteInt((sign << 24 | 0x10000000) | (int)v);
			}
			else if(v<int.MaxValue)
			{
				writeUnsignedByte(sign | 8);
				natureWriteInt((int)v);
			}
			else
			{
				writeUnsignedByte(sign | 4);
				natureWriteLong(v);
			}
		}

		/// <summary>
		/// 原版写long
		/// </summary>
		/// <param name="value"></param>
		public void natureWriteLong(long value)
		{
			if(!ensureCanWrite(8))
				return;

			int pos=_position;

			_position+=8;

			byte[] buf=_buf;

			buf[pos++]=(byte)(value >> 56);
			buf[pos++]=(byte)(value >> 48);
			buf[pos++]=(byte)(value >> 40);
			buf[pos++]=(byte)(value >> 32);
			buf[pos++]=(byte)(value >> 24);
			buf[pos++]=(byte)(value >> 16);
			buf[pos++]=(byte)(value >> 8);
			buf[pos]=(byte)(value);
		}

		/// <summary>
		/// 写入一字符串
		/// </summary>
		/// <param name="str"></param>
		public void writeUTFBytes(string str)
		{
			if(str==null)
			{
				throwError("字符串不能为空");
				str="";
			}

			byte[] bb=Encoding.UTF8.GetBytes(str);

			writeByteArr(bb);
		}

		/// <summary>
		/// 写入一字符串，前面加上UnsignedShort长度前缀
		/// </summary>
		public void writeUTF(string str)
		{
			if(str==null)
			{
				throwError("字符串不能为空");
				str="";
			}

			byte[] bb=Encoding.UTF8.GetBytes(str);

			writeLen(bb.Length);

			this.writeByteArr(bb);
		}

		/// <summary>
		/// 写一个长度(只处理正整数)
		/// </summary>
		public void writeLen(int value)
		{
			if(_writeLenLimit>0 && value>=_writeLenLimit)
			{
				if(ShineSetting.needBytesLenLimitError)
					throwError("writeLen,超过长度限制:" + value);
			}

			if(value<0)
			{
				writeByte(0);
			}
			else if(value<0x80)
			{
				writeUnsignedByte(value | 0x80);
			}
			else if(value<0x4000)
			{
				natureWriteShort(value | 0x4000);
			}
			else if(value<0x20000000)
			{
				natureWriteInt(value | 0x20000000);
			}
			else
			{
				writeByte(0x10);
				natureWriteInt(value);
			}
		}

		/** 设置写入长度限制 */
		public void setWriteLenLimit(int value)
		{
			_writeLenLimit=value;
		}

		public int getWriteLenLimit()
		{
			return _writeLenLimit;
		}

		//--len--//

		/// <summary>
		/// 获取长度尺寸
		/// </summary>
		public static int getLenSize(int value)
		{
			if(value<0)
			{
				Ctrl.throwError("长度不能小于0",value);
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

		/** 开始写对象 */
		public void startWriteObj()
		{
			if(_useBitBoolean)
			{
				getWriteStack().add3(_position,_booleanBufPos,_booleanBitIndex);
				clearBooleanPos();
			}
			else
			{
				getWriteStack().add(_position);
			}
		}

		/** 结束写对象 */
		public void endWriteObj()
		{
			//倒序
			if(_useBitBoolean)
			{
				_booleanBitIndex=_writeStack.pop();
				_booleanBufPos=_writeStack.pop();
			}

			int pos=_writeStack.pop();

			insertLenToPos(pos);
		}

		//--data--//

		/// <summary>
		/// 写入一个非空数据(可继承的)(完整版)
		/// </summary>
		public void writeDataFullNotNull(BaseData data)
		{
			startWriteObj();

			int dataID=data.getDataID();

			if(dataID<=0)
			{
				Ctrl.log("不该找不到dataID",dataID);
				this.writeShort(-1);

			}
			else
			{
				this.writeShort(dataID);

				data.writeBytesFullWithoutLen(this);
			}

			endWriteObj();
		}

		/// <summary>
		/// 写入数据(考虑空)(可继承的)(完整版)
		/// </summary>
		public void writeDataFull(BaseData data)
		{
			if(data!=null)
			{
				this.writeBoolean(true);

				writeDataFullNotNull(data);
			}
			else
			{
				this.writeBoolean(false);
			}
		}

		/// <summary>
		/// 写入一个非空数据(完整版)
		/// </summary>
		public void writeDataSimpleNotNull(BaseData data)
		{
			int dataID=data.getDataID();

			if(dataID<=0)
			{
				Ctrl.log("不该找不到dataID",dataID);
				this.writeShort(-1);
			}
			else
			{
				this.writeShort(dataID);
				data.writeBytesSimple(this);
			}
		}

		/// <summary>
		/// 写入数据(考虑空)(完整版)
		/// </summary>
		public void writeDataSimple(BaseData data)
		{
			if(data!=null)
			{
				this.writeBoolean(true);

				writeDataSimpleNotNull(data);
			}
			else
			{
				this.writeBoolean(false);
			}
		}

		//check

		/// <summary>
		/// 获取某一段的字节hash(short)
		/// </summary>
		public short getHashCheck(int pos,int length)
		{
			return BytesUtils.getHashCheck(_buf,pos,length);
		}

		//--stream--//

		/// <summary>
		/// 将流写入自身
		/// </summary>
		public void writeBytesStream(BytesWriteStream stream,int pos,int length)
		{
			if(stream.length()<pos + length)
			{
				tailError();
				return;
			}

			writeByteArr(stream._buf,pos,length);
		}

		//compress

		/// <summary>
		/// 压缩
		/// </summary>
		public void compress()
		{
			byte[] b=BytesUtils.compressByteArr(_buf,0,_length);

			_buf=b;
			_position=0;
			_length=b.Length;
		}

		public override string ToString()
		{
			return BytesUtils.bytesToString(_buf,0,_length);
		}

		/** 写版本号 */
		public void writeVersion(int v)
		{
			natureWriteInt(v);
		}

		/** 在指定位置插入一个(当前Position与pos之差),之后设置为 当前位置+size */
		public void insertLenToPos(int pos)
		{
			insertLenToPos(pos,getPosition()-pos);
		}

		/** 在指定位置插入一个len,之后设置为 当前位置+size */
		public void insertLenToPos(int pos,int len)
		{
			int cp=getPosition();

			setPosition(pos);

			int size=getLenSize(len);

			insertVoidBytes(size,len);

			writeLen(len);

			setPosition(cp+size);
		}

		/** 移除前部 */
		public void removeFront(int len)
		{
			if(_length<len)
				return;

			if(_length==len)
			{
				clear();
			}
			else
			{
				int last=_length - len;

				Buffer.BlockCopy(_buf,len,_buf,0,last);
				_length=last;
				_position-=len;
			}
		}

		//unity
		
		public void writeVector3(in Vector3 vec)
		{
			writeFloat(vec.x);
			writeFloat(vec.y);
			writeFloat(vec.z);
		}
	}
}