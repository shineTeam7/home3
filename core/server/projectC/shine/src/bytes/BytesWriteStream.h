#pragma once

#include "../SInclude.h"
#include "../utils/MathUtils.h"
#include "../utils/ObjectUtils.h"
#include "BytesReadStream.h"
#include "../utils/BytesUtils.h"
#include "../global/ShineSetting.h"
#include "../support/collection/SList.h"
#include <cstring>

using namespace std;

/** 字节写流 */
class BytesWriteStream
{
private:
	char* _buf = nullptr;
	int _bufSize = 0;

	bool _selfNewBuf = false;

	int _length = 0;
	int _position = 0;

	/** 是否可扩容 */
	bool _canGrow = true;
	/** 写入长度限制(防溢出) */
	int _writeLenLimit = 0;

	int _booleanBufPos = -1;
	int _booleanBitIndex = 0;

	SList<int> _writeStack;

public:
	BytesWriteStream() :BytesWriteStream(8)
	{

	}

	~BytesWriteStream();

	BytesWriteStream(int size);

	BytesWriteStream(char* buf, int length)
	{
		setBuf(buf, length, length);
	}

	BytesWriteStream(char* buf, int bufSize, int length)
	{
		setBuf(buf, bufSize, length);
	}

	void setBuf(char* buf, int bufSize, int length);

	/** 获取字节数组 */
	char* getBuf()
	{
		return _buf;
	}

	/** 得到字节流位置 */
	int getPosition()
	{
		return _position;
	}

	/** 设置写字节的偏移量 */
	void setPosition(int pos);

	/** 得到字节数组的长度 */
	int length()
	{
		return _length;
	}

	/** 设置长度(只可增) */
	void setLength(int len);


	/** 获取字节 */
	char getByte(int index)
	{
		return _buf[index];
	}

	/** 得到字节缓存的字节数组(副本)(从position 0到length) */
	char* getByteArray()
	{
		char* re = new char[_length];
		memcpy(re, _buf, _length);

		return re;
	}

	/** 清空(不清off) */
	void clear()
	{
		_position = 0;
		_length = 0;
		_writeLenLimit = 0;
	}

	void throwError(string str)
	{
		Ctrl::throwError(str);
	}

	/** 写入字节组(不带头长) */
	void writeByteArr(char* bbs, int length)
	{
		writeByteArr(bbs, 0, length);
	}

	/** 写入字节组 */
	void writeByteArr(char* bbs, int off, int length);

	/** 在当前position插入空字节组(后面的后移,position不变) */
	void insertVoidBytes(int num, int length);

	/** 写入一个布尔值 */
	void writeBoolean(bool value);

	/** 写入一个字节 */
	void writeByte(int value);

	/** 写入一个无符号的短整型数值 */
	void writeUnsignedByte(int value);

	/** 写入一个短整型数值 */
	void writeShort(int value);

	/** 原版写short */
	void natureWriteShort(int value);

	/** 写入一个无符号的短整型数值 */
	void writeUnsignedShort(int value);

	/** 原版写ushort */
	void natureWriteUnsignedShort(int value);

	/** 写入一个整型数值 */
	void writeInt(int value);

	/** 原版写int */
	void natureWriteInt(int value);

	/** 写入一个浮点值 */
	void writeFloat(float value);

	/** 写一个双精数 */
	void writeDouble(double value);

	/** 写入一个长整型数值 */
	void writeLong(int64 value);

	/** 原版写long */
	void natureWriteLong(int64 value);

	/** 写入一字符串 */
	void writeUTFBytes(const string& str);

	/** 写入一字符串，前面加上UnsignedShort长度前缀 */
	void writeUTF(const string& str);

	/** 写一个长度(只处理正整数) */
	void writeLen(int value);

	/** 设置写入长度限制 */
	void setWriteLenLimit(int value)
	{
		_writeLenLimit = value;
	}

	/** 写入内存 */
	void writeMem(void* ptr, int len);

	void clearBooleanPos();

	//--len--//

	/** 获取长度尺寸 */
	static int getLenSize(int value);

	/** 开始写对象 */
	void startWriteObj();

	/** 结束写对象 */
	void endWriteObj();

	//--data--//

	///** 写入一个非空数据(可继承的)(完整版) */
	//void writeDataFullNotNull(BaseData data)
	//{
	//	if (data == null)
	//	{
	//		if (!ShineSetting.isDBUpdate)
	//		{
	//			throwError("writeDataFullNotNull时,写入的数据为空");
	//		}

	//		return;
	//	}

	//	int position = startWriteObj();

	//	int dataID = data.getDataID();

	//	if (dataID > 0)
	//	{
	//		this.writeShort(dataID);

	//		data.writeBytesFullWithoutLen(this);
	//	}
	//	else
	//	{
	//		Ctrl.log("不该找不到dataID");
	//		this.writeShort(-1);
	//	}

	//	endWriteObj(position);
	//}

	///** 写入数据(考虑空)(可继承的)(完整版) */
	//void writeDataFull(BaseData data)
	//{
	//	if (data != null)
	//	{
	//		this.writeBoolean(true);

	//		writeDataFullNotNull(data);
	//	}
	//	else
	//	{
	//		this.writeBoolean(false);
	//	}
	//}

	///** 写入一个非空数据(可继承的)(简版) */
	//void writeDataSimpleNotNull(BaseData data)
	//{
	//	int dataID = data.getDataID();

	//	if (dataID > 0)
	//	{
	//		this.writeShort(dataID);

	//		data.writeBytesSimple(this);
	//	}
	//	else
	//	{
	//		Ctrl.log("不该找不到dataID");
	//		this.writeShort(-1);
	//	}
	//}

	///** 写入数据(考虑空)(简版) */
	//void writeDataSimple(BaseData data)
	//{
	//	if (data != null)
	//	{
	//		this.writeBoolean(true);

	//		writeDataSimpleNotNull(data);
	//	}
	//	else
	//	{
	//		this.writeBoolean(false);
	//	}
	//}

	//--check--//

	///** 获取某一段的字节hash(short) */
	//public short getHashCheck(int pos, int length)
	//{
	//	return BytesUtils.getHashCheck(_buf, pos, length);
	//}

	//--stream--//

	/** 写读流 */
	void writeReadStream(BytesReadStream stream)
	{
		writeByteArr(stream.getBuf(), stream.getPosition(), stream.bytesAvailable());
	}

	/** 将流写入自身 */
	void writeBytesStream(BytesWriteStream stream)
	{
		writeBytesStream(stream, 0, stream.length());
	}

	/** 将流写入自身 */
	void writeBytesStream(BytesWriteStream stream, int pos, int length);

	//compress

	/** 压缩(0-length)(无视postion) */
	/*oid compress()
	{
		byte[] b = BytesUtils.compressByteArr(_buf, 0, _length);

		_buf = b;
		_position = 0;
		_length = b.length;
	}*/

	//ex

	///** 写入netty字节组 */
	//void writeByteBuf(ByteBuf buf)
	//{
	//	int len = buf.readableBytes();

	//	if (len <= 0)
	//	{
	//		return;
	//	}

	//	if (_position + len > _length)
	//	{
	//		if (_canGrow)
	//		{
	//			setLength(_position + len);
	//		}
	//		else
	//		{
	//			tailError();
	//			return;
	//		}
	//	}

	//	buf.getBytes(buf.readerIndex(), _buf, _position, len);

	//	_position += len;
	//}

	/** 转化为string */
	string toString()
	{
		return BytesUtils::bytesToString(_buf, 0, _length);
	}

	/** 写版本号 */
	void writeVersion(int v)
	{
		natureWriteInt(v);
	}

	/** 在指定位置插入一个(当前Position与pos之差),之后设置为 当前位置+size */
	void insertLenToPos(int pos)
	{
		insertLenToPos(pos, getPosition() - pos);
	}

	/** 在指定位置插入一个len,之后设置为 当前位置+size */
	void insertLenToPos(int pos, int len);

	/** 写入向量 */
	void writeVector3(Vector3& vec);

private:

	/** 扩容 */
	void grow(int len);

	//--write--//

	/** 确认可写 */
	bool ensureCanWrite(int len);

	/** 遇到文件尾 */
	void tailError();
};
