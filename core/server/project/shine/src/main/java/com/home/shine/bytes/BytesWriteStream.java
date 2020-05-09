package com.home.shine.bytes;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.pool.PoolObject;
import com.home.shine.utils.BytesUtils;
import com.home.shine.utils.MathUtils;
import io.netty.buffer.ByteBuf;

/** 字节写流 */
public class BytesWriteStream extends PoolObject
{
	private byte[] _buf;
	
	private int _length=0;
	
	private int _position=0;
	
	/** 是否可扩容 */
	private boolean _canGrow=true;
	/** 写入长度限制(防溢出) */
	private int _writeLenLimit=0;
	
	private int _booleanBufPos=-1;
	private int _booleanBitIndex=0;
	
	private IntList _writeStack;
	
	public BytesWriteStream()
	{
		this(8);
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
	}
	
	/** 设置操作字节流 */
	public void setBuf(byte[] buf)
	{
		_canGrow=false;
		
		_buf=buf;
		_length=buf.length;
		
		_position=0;
		_writeLenLimit=0;
	}
	
	/** 获取字节数组 */
	public byte[] getBuf()
	{
		return _buf;
	}
	
	/** 得到字节流位置 */
	public int getPosition()
	{
		return _position;
	}
	
	/** 设置写字节的偏移量 */
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
	
	/** 得到字节数组的长度 */
	public int length()
	{
		return _length;
	}
	
	/** 设置长度(只可增) */
	public void setLength(int len)
	{
		if(len<=_length)
		{
			return;
		}
		
		//超过了再grow
		if(len>_buf.length)
		{
			grow(len);
		}
		
		_length=len;
	}
	
	/** 遇到文件尾 */
	private void tailError()
	{
		int pos=_position;
		
		setPosition(_length);
		
		throwError("遇到文件尾,pos:"+pos+" length:"+_length);
	}
	
	/** 扩容 */
	private void grow(int len)
	{
		int cap;
		
		if((cap=MathUtils.getPowerOf2(len))>_buf.length)
		{
			byte[] bs=new byte[cap];
			
			System.arraycopy(_buf,0,bs,0,_length);
			
			_buf=bs;
		}
	}
	
	/** 获取字节 */
	public byte getByte(int index)
	{
		return _buf[index];
	}
	
	/** 得到字节缓存的字节数组(副本)(从position 0到length) */
	public byte[] getByteArray()
	{
		byte[] re=new byte[_length];
		System.arraycopy(_buf,0,re,0,_length);
		
		return re;
	}
	
	/** 清空(不清off) */
	@Override
	public void clear()
	{
		_position=0;
		_length=0;
		_writeLenLimit=0;
		
		clearBooleanPos();
		if(_writeStack!=null)
			_writeStack.clear();
	}
	
	/** 抛错 */
	public void throwError(String str)
	{
		Ctrl.throwError(str);
	}
	
	public void clearBooleanPos()
	{
		if(!ShineSetting.bytesUseBitBoolean)
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
	private boolean ensureCanWrite(int len)
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
	
	/** 写入字节组(不带头长) */
	public void writeByteArr(byte[] bbs)
	{
		writeByteArr(bbs,0,bbs.length);
	}
	
	/** 写入字节组 */
	public void writeByteArr(byte[] bbs,int off,int length)
	{
		if(!ensureCanWrite(length))
		{
			return;
		}
		
		System.arraycopy(bbs,off,_buf,_position ,length);
		
		_position+=length;
	}
	
	/** 在当前position插入空字节组(后面的后移,position不变) */
	public void insertVoidBytes(int num,int length)
	{
		if(!ensureCanWrite(num + length))
		{
			return;
		}
		
		System.arraycopy(_buf,_position,_buf,_position + num,length);
	}
	
	/** 写入一个布尔值 */
	public void writeBoolean(boolean value)
	{
		if(ShineSetting.bytesUseBitBoolean)
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
					_buf[_booleanBufPos] |= 1<<_booleanBitIndex;
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
	
	/** 写入一个字节 */
	public void writeByte(int value)
	{
		if(!ensureCanWrite(1))
		{
			return;
		}
		
		_buf[_position++]=(byte)value;
	}
	
	/** 写入一个无符号的短整型数值 */
	public void writeUnsignedByte(int value)
	{
		if(!ensureCanWrite(1))
		{
			return;
		}
		
		_buf[_position++]=(byte)value;
	}
	
	/** 写入一个短整型数值 */
	public void writeShort(int value)
	{
		int sign=value<0 ? 0x80 : 0;
		int v=value >= 0 ? value : -value;
		
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
	
	/** 原版写short */
	public void natureWriteShort(int value)
	{
		if(!ensureCanWrite(2))
		{
			return;
		}
		
		int pos=_position;
		
		byte[] buf=_buf;
		
		buf[pos]=(byte)(value >>> 8);
		buf[pos + 1]=(byte)(value);
		
		_position+=2;
	}
	
	/** 写入一个无符号的短整型数值 */
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
	
	/** 原版写ushort */
	public void natureWriteUnsignedShort(int value)
	{
		if(!ensureCanWrite(2))
		{
			return;
		}
		
		int pos=_position;
		
		byte[] buf=_buf;
		
		buf[pos]=(byte)(value >>> 8);
		buf[pos + 1]=(byte)(value);
		
		_position+=2;
	}
	
	/** 写入一个整型数值 */
	public void writeInt(int value)
	{
		int sign=value<0 ? 0x80 : 0;
		int v=value >= 0 ? value : -value;
		
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
	
	public static int encodeZigZag32(int n)
	{
		return n << 1 ^ n >> 31;
	}
	
	public void writeSInt(int value)
	{
		value=encodeZigZag32(value);
		
		while((value & -128) != 0) {
			ensureCanWrite(1);
			_buf[_position++] = (byte)(value & 127 | 128);
			value >>>= 7;
		}
		
		ensureCanWrite(1);
		_buf[_position++] = (byte)value;
	}
	
	/** 原版写int */
	public void natureWriteInt(int value)
	{
		if(!ensureCanWrite(4))
		{
			return;
		}
		
		int pos=_position;
		
		byte[] buf=_buf;
		
		buf[pos]=(byte)(value >>> 24);
		buf[pos + 1]=(byte)(value >>> 16);
		buf[pos + 2]=(byte)(value >>> 8);
		buf[pos + 3]=(byte)(value);
		
		_position+=4;
	}
	
	/** 写入一个浮点值 */
	public void writeFloat(float value)
	{
		natureWriteInt(Float.floatToIntBits(value));
	}
	
	/** 写一个双精数 */
	public void writeDouble(double value)
	{
		natureWriteLong(Double.doubleToLongBits(value));
	}
	
	/** 写入一个长整型数值 */
	public void writeLong(long value)
	{
		int sign=value<0 ? 0x80 : 0;
		long v=value >= 0 ? value : -value;
		
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
		else if(v<Integer.MAX_VALUE)
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
	
	/** 原版写long */
	public void natureWriteLong(long value)
	{
		if(!ensureCanWrite(8))
		{
			return;
		}
		
		int pos=_position;
		
		byte[] buf=_buf;
		
		buf[pos]=(byte)(value >>> 56);
		buf[pos + 1]=(byte)(value >>> 48);
		buf[pos + 2]=(byte)(value >>> 40);
		buf[pos + 3]=(byte)(value >>> 32);
		buf[pos + 4]=(byte)(value >>> 24);
		buf[pos + 5]=(byte)(value >>> 16);
		buf[pos + 6]=(byte)(value >>> 8);
		buf[pos + 7]=(byte)(value);
		
		_position+=8;
	}
	
	/** 写入一字符串 */
	public void writeUTFBytes(String str)
	{
		if(str==null)
		{
			throwError("字符串不能为空");
			str="";
		}
		
		byte[] bytes=str.getBytes(BytesUtils.UTFCharset);
		
		writeByteArr(bytes);
	}
	
	/** 写入一字符串，前面加上UnsignedShort长度前缀 */
	public void writeUTF(String str)
	{
		if(str==null)
		{
			throwError("字符串不能为空");
			str="";
		}
		
		byte[] bytes=str.getBytes(BytesUtils.UTFCharset);
		
		writeLen(bytes.length);
		
		this.writeByteArr(bytes);
	}
	
	/** 写一个长度(只处理正整数) */
	public void writeLen(int value)
	{
		if(_writeLenLimit>0 && value>=_writeLenLimit)
		{
			if(ShineSetting.needBytesLenLimitError)
				Ctrl.errorLog("writeLen,超过长度限制:" + value);
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
	
	//--len--//
	
	/** 获取长度尺寸 */
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
		if(ShineSetting.bytesUseBitBoolean)
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
		if(ShineSetting.bytesUseBitBoolean)
		{
			_booleanBitIndex=_writeStack.pop();
			_booleanBufPos=_writeStack.pop();
		}
		
		int pos=_writeStack.pop();

		insertLenToPos(pos);
	}
	
	///** 初始化读取字节(返回当前位置) */
	//public static int getWriteBytes(BytesWriteStream stream)
	//{
	//	return stream.startWriteObj();
	//}
	//
	///** 回归读取字节 */
	//public static void disWriteBytes(BytesWriteStream stream,int position)
	//{
	//	stream.endWriteObj(position);
	//}
	
	//--data--//
	
	/** 写入一个非空数据(可继承的)(完整版) */
	public void writeDataFullNotNull(BaseData data)
	{
		if(data==null)
		{
			if(!ShineSetting.isDBUpdate)
			{
				throwError("writeDataFullNotNull时,写入的数据为空");
			}
			
			return;
		}
		
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
	
	/** 写入数据(考虑空)(可继承的)(完整版) */
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
	
	/** 写入一个非空数据(可继承的)(简版) */
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
	
	/** 写入数据(考虑空)(简版) */
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
	
	//--check--//
	
	/** 获取某一段的字节hash(short) */
	public short getHashCheck(int pos,int length)
	{
		return BytesUtils.getHashCheck(_buf,pos,length);
	}
	
	//--stream--//
	
	/** 写读流 */
	public void writeReadStream(BytesReadStream stream)
	{
		writeByteArr(stream.getBuf(),stream.getPositionOff(),stream.bytesAvailable());
	}
	
	/** 将流写入自身 */
	public void writeBytesStream(BytesWriteStream stream)
	{
		writeBytesStream(stream,0,stream.length());
	}
	
	/** 将流写入自身 */
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
	
	/** 压缩(0-length)(无视postion) */
	public void compress()
	{
		byte[] b=BytesUtils.compressByteArr(_buf,0,_length);
		
		_buf=b;
		_position=0;
		_length=b.length;
	}
	
	//ex
	
	/** 写入netty字节组 */
	public void writeByteBuf(ByteBuf buf)
	{
		int len=buf.readableBytes();
		
		if(len<=0)
		{
			return;
		}
		
		if(_position + len>_length)
		{
			if(_canGrow)
			{
				setLength(_position + len);
			}
			else
			{
				tailError();
				return;
			}
		}
		
		buf.getBytes(buf.readerIndex(),_buf,_position,len);
		
		_position+=len;
	}
	
	@Override
	public String toString()
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
		
		insertVoidBytes(size,cp-pos);
		
		writeLen(len);
		
		setPosition(cp+size);
	}
	
	public static BytesWriteStream create()
	{
		return new BytesWriteStream();
	}
	
	public static BytesWriteStream create(int size)
	{
		return new BytesWriteStream(size);
	}
	
	public static BytesWriteStream create(byte[] buf)
	{
		return new BytesWriteStream(buf);
	}
}
