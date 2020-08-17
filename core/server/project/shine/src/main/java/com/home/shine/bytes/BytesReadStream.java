package com.home.shine.bytes;

import com.home.shine.control.BytesControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntList;
import com.home.shine.support.pool.DataPool;
import com.home.shine.utils.BytesUtils;
import com.home.shine.utils.ObjectUtils;

/** 字节读流 */
public class BytesReadStream
{
	/** 是否使用bitBoolean */
	private boolean _useBitBoolean=ShineSetting.bytesUseBitBoolean;
	
	private byte[] _buf=BytesUtils.EmptyByteArr;
	
	protected int _off=0;
	
	protected int _length=0;
	
	protected int _position=0;
	
	//read
	/** 读取长度限制(防溢出) */
	protected int _readLenLimit=0;
	/** 读取次数 */
	protected int _readNum=0;
	/** 读锁,只为加速运算 */
	protected boolean _readLock=false;
	
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
	
	public void setUseBitBoolean(boolean value)
	{
		_useBitBoolean=value;
	}
	
	/** 设置操作字节流 */
	public void setBuf(byte[] buf)
	{
		setBuf(buf,0,buf.length);
	}
	
	/** 设置操作字节流 */
	public void setBuf(byte[] buf,int off,int length)
	{
		if(off>buf.length)
		{
			off=buf.length;
		}
		
		if(length + off>buf.length)
		{
			length=buf.length - off;
		}
		
		clear();
		
		_buf=buf;
		_off=off;
		_length=length;
	}
	
	/** 获取获取字节数组 */
	public byte[] getBuf()
	{
		return _buf;
	}
	
	/** 得到字节流位置 */
	public int getPosition()
	{
		return _position;
	}
	
	/** 得到字节流位置 */
	public int getPositionOff()
	{
		return _position+_off;
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
	
	/** 设置长度限制(可缩短) */
	public void setLength(int len)
	{
		if(len + _off>_buf.length)
		{
			tailError();
		}
		
		_length=len;
	}
	
	/** 剩余可读数目 */
	public int bytesAvailable()
	{
		return _length - _position;
	}
	
	/** 是否已为空(没有剩余字节) */
	public boolean isEmpty()
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
	
	/** 获取字节 */
	public byte getByte(int index)
	{
		return _buf[index + _off];
	}
	
	/** 得到字节缓存的字节数组(副本)(从0到length) */
	public byte[] getByteArray()
	{
		byte[] re=new byte[_length];
		System.arraycopy(_buf,_off,re,0,_length);
		
		return re;
	}
	
	/** 得到字节缓存的字节数组(副本)(从position 0到length) */
	public byte[] getByteArrayFromPos()
	{
		byte[] re=new byte[_length-_position];
		System.arraycopy(_buf,_off+_position,re,0,re.length);
		
		return re;
	}
	
	/** 清空(不清off) */
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
	public void throwError(String str)
	{
		Ctrl.throwError(str);
	}
	
	/** 抛出类型转发错误 */
	public void throwTypeReadError(Class<?> cls1,Class<?> cls2)
	{
		throwError("读取时，类型升级失败"+cls1.getName()+" "+cls2.getName());
	}
	
	//--read--//
	
	private boolean ensureCanRead(int len)
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
	
	/** 读出字节组(字节读写用) */
	public boolean readByteArr(byte[] bytes,int off,int len)
	{
		if(!ensureCanRead(len))
			return false;
		
		System.arraycopy(_buf,_position + _off,bytes,off,len);
		
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
		
		System.arraycopy(_buf,_position + _off,re,0,len);
		
		_position+=len;
		
		return re;
	}

	/** 读出一个布尔值 */
	public boolean readBoolean()
	{
		if(_useBitBoolean)
		{
			//新的
			if(_booleanBitIndex==-1)
			{
				if(!ensureCanRead(1))
					return false;
				
				_booleanBitValue=_buf[_position++ + _off] & 0xff;
				
				
				boolean re=(_booleanBitValue & 1)!=0;
				_booleanBitIndex=1;
				return re;
			}
			else
			{
				boolean re=(_booleanBitValue & (1<<_booleanBitIndex))!=0;
				
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
	
	/** 读出一个字节 */
	public byte readByteB()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		return _buf[_position++ + _off];
	}

	/** 读出一个字节 */
	public int readByte()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		return _buf[_position++ + _off];
	}

	/** 读出一个无符号字节 */
	public int readUnsignedByte()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		return _buf[_position++ + _off] & 0xff;
	}

	/** 读出一个短整型数值 */
	public int readShort()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		int v=_buf[_position + _off];
		
		int sign=v & 0x80;
		
		int n=v & 0x7f;
		
		int re;
		
		if(n >= 0x40)
		{
			++_position;
			re=n & 0x3f;
		}
		else if(n >= 0x20)
		{
			re=natureReadShort() & 0x1fff;
		}
		else if(n >= 0x10)
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
	
	/** 原版读short */
	public int natureReadShort()
	{
		if(!ensureCanRead(2))
		{
			return 0;
		}
		
		int pos=_position + _off;
		_position+=2;
		
		byte[] buf=_buf;
		
		return (short)((buf[pos + 1] & 0xff) | ((buf[pos] & 0xff) << 8));
	}
	
	/** 读ushort */
	public int readUnsignedShort()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		int n=_buf[_position + _off] & 0xff;
		
		int re;
		
		if(n >= 0x80)
		{
			++_position;
			re=n & 0x7f;
		}
		else if(n >= 0x40)
		{
			re=natureReadUnsignedShort() & 0x3fff;
		}
		else if(n >= 0x20)
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
	
	/** 原版读ushort */
	public int natureReadUnsignedShort()
	{
		if(!ensureCanRead(2))
		{
			return 0;
		}
		
		int pos=_position + _off;
		_position+=2;
		
		byte[] buf=_buf;
		
		return (buf[pos + 1] & 0xff) | ((buf[pos] & 0xff) << 8);
	}

	/** 读出一个整型数值 */
	public int readInt()
	{
		if(!ensureCanRead(1))
		{
			return 0;
		}
		
		int v=_buf[_position + _off];
		
		int sign=v & 0x80;
		
		int n=v & 0x7f;
		
		int re;
		
		if(n >= 0x40)
		{
			++_position;
			re=n & 0x3f;
		}
		else if(n >= 0x20)
		{
			re=natureReadShort() & 0x1fff;
		}
		else if(n >= 0x10)
		{
			re=natureReadInt() & 0x0fffffff;
		}
		else if(n >= 8)
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
	
	public static int decodeZigZag32(int n) {
		return n >>> 1 ^ -(n & 1);
	}
	
	public int readRawVarInt32()
	{
		if(!ensureCanRead(1))
			return 0;
		
		byte[] buf = this._buf;
		int x;
		if ((x = buf[_position++]) >= 0) {
			return x;
		}
		
		if(!ensureCanRead(1))
			return 0;
		
		if ((x = x ^ buf[_position++] << 7) < 0) {
			x ^= -128;
			return x;
		}
		
		if(!ensureCanRead(1))
			return 0;
		
		if ((x ^= buf[_position++] << 14) >= 0) {
			x ^= 16256;
			return x;
		}
		
		if(!ensureCanRead(1))
			return 0;
		
		if ((x ^= buf[_position++] << 21) < 0) {
			x ^= -2080896;
			return x;
		}
		
		if(!ensureCanRead(1))
			return 0;
		
		if ((x ^= buf[_position++] << 28) < 0) {
			x ^= 266354560;
			return x;
		}
		
		throwError("readSInt,invalid number:" + x);
		
		return 0;
	}
	
	public int readSInt()
	{
		return decodeZigZag32(readRawVarInt32());
	}
	
	/** 原版读int */
	public int natureReadInt()
	{
		if(!ensureCanRead(4))
		{
			return 0;
		}
		
		int pos=_position + _off;
		
		_position+=4;
		
		byte[] buf=_buf;
		
		return (buf[pos + 3] & 0xff) | ((buf[pos + 2] & 0xff) << 8) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos] & 0xff) << 24);
	}

	/** 读出一个浮点数 */
	public float readFloat()
	{
		if(_readLock)
		{
			return 0f;
		}
		
		return Float.intBitsToFloat(natureReadInt());
	}
	
	/** 读出一个双精数 */
	public double readDouble()
	{
		if(_readLock)
		{
			return 0.0;
		}
		
		return Double.longBitsToDouble(natureReadLong());
	}
	
	/** 读出一个长整型数值 */
	public long readLong()
	{
		if(!ensureCanRead(1))
		{
			return 0L;
		}
		
		int v=_buf[_position + _off];
		
		int sign=v & 0x80;
		
		int n=v & 0x7f;
		
		long re;
		
		if(n >= 0x40)
		{
			++_position;
			re=n & 0x3f;
		}
		else if(n >= 0x20)
		{
			re=natureReadShort() & 0x1fff;
		}
		else if(n >= 0x10)
		{
			re=natureReadInt() & 0x0fffffff;
		}
		else if(n >= 8)
		{
			++_position;
			re=natureReadInt();
		}
		else if(n >= 4)
		{
			++_position;
			re=natureReadLong();
		}
		else
		{
			throwError("readLong,invalid number:" + n);
			re=0;
		}
		
		return sign>0 ? -re : re;
	}
	
	/** 原版读long */
	public long natureReadLong()
	{
		if(!ensureCanRead(8))
		{
			return 0L;
		}
		
		int pos=_position + _off;
		_position+=8;
		
		byte[] buf=_buf;
		
		return (buf[pos + 7] & 0xffL) | ((buf[pos + 6] & 0xffL) << 8) | ((buf[pos + 5] & 0xffL) << 16) | ((buf[pos + 4] & 0xffL) << 24) | ((buf[pos + 3] & 0xffL) << 32) | ((buf[pos + 2] & 0xffL) << 40) | ((buf[pos + 1] & 0xffL) << 48) | ((buf[pos] & 0xffL) << 56);
	}
	
	/** 读出指定长的字符串 */
	public String readUTFBytes(int length)
	{
		if(!ensureCanRead(length))
		{
			return "";
		}
		
		if(length==0)
		{
			return "";
		}
		
		int pos=_position + _off;
		
		_position+=length;
		
		return new String(_buf,pos,length,BytesUtils.UTFCharset);
	}
	
	/** 读出一个字符串，在最前用一UnsignedShort表示字符长度 */
	public final String readString()
	{
		return readUTF();
	}
	
	/** 读出一个字符串，在最前用一UnsignedShort表示字符长度 */
	public String readUTF()
	{
		if(_readLock)
		{
			return "";
		}
		
		return readUTFBytes(readLen());
	}
	
	/** 读一个长度 */
	public int readLen()
	{
		//这里不走ensure
		if(_readLock)
		{
			return 0;
		}
		
		if(_position + 1>_length)
		{
			return 0;
		}
		
		int re;
		int n=_buf[_position + _off] & 0xff;
		
		if(n >= 0x80)
		{
			++_position;
			re= n & 0x7f;
		}
		else if(n >= 0x40)
		{
			if(_position + 2>_length)
			{
				return 0;
			}
			
			re= natureReadUnsignedShort() & 0x3fff;
		}
		else if(n >= 0x20)
		{
			if(_position + 4>_length)
			{
				return 0;
			}
			
			re= natureReadInt() & 0x1fffffff;
		}
		else if(n >= 0x10)
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
				Ctrl.errorLog("readLen,超过长度限制:" + re);
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
	
	//--len--//
	
	///** 获取一个读取字节 */
	//public static int getReadBytes(BytesReadStream stream)
	//{
	//	return stream.startReadObj();
	//}
	//
	///** 销毁读取字节 */
	//public static void disReadBytes(BytesReadStream stream,int position)
	//{
	//	stream.endReadObj(position);
	//}
	
	//--data--//
	
	/** 读出一个非空数据(完整版)(stream空时，读出来为null) */
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
	
	/** 读取数据(考虑空)(可继承的)(完整版) */
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
	
	/** 读出一个非空数据(可继承的)(简版) */
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
	
	/** 读取数据(考虑空)(简版) */
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
	
	/** 获取某一段的字节hash(short) */
	public short getHashCheck(int pos,int length)
	{
		return BytesUtils.getHashCheck(_buf,pos + _off,length);
	}
	
	//uncompress
	
	/** 解压缩(0-length)无视position */
	public void unCompress()
	{
		byte[] b=BytesUtils.uncompressByteArr(_buf,_off,_length);
		
		_buf=b;
		_off=0;
		_position=0;
		_length=b.length;
	}
	
	/** 克隆一个 */
	public BytesReadStream clone()
	{
		BytesReadStream re=BytesReadStream.create();
		re._buf=_buf;
		re._off=_off;
		re._position=_position;
		re._length=_length;
		
		return re;
	}
	
	@Override
	public String toString()
	{
		return BytesUtils.bytesToString(_buf,_off,_length);
	}
	
	/** 检查版本 */
	public boolean checkVersion(int version)
	{
		if(bytesAvailable()<4)
			return false;
		
		return natureReadInt()==version;
	}
	
	/** 创建数据 */
	public BaseData createData(int type)
	{
		if(_pool!=null)
			return _pool.createData(type);
		
		return BytesControl.getDataByID(type);
	}
	
	//ex方法
	
	public void setDataPool(DataPool pool)
	{
		_pool=pool;
	}
	
	public static BytesReadStream create()
	{
		return new BytesReadStream();
	}
	
	public static BytesReadStream create(int size)
	{
		return new BytesReadStream(size);
	}
	
	public static BytesReadStream create(byte[] buf)
	{
		return new BytesReadStream(buf);
	}
	
	public static BytesReadStream create(byte[] buf,int off,int length)
	{
		return new BytesReadStream(buf,off,length);
	}
}
