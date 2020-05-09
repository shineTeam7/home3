#pragma once
#include "../SInclude.h"
#include "../utils/ObjectUtils.h"
#include "../data/BaseData.h"
#include "../control/BytesControl.h"
#include "../utils/BytesUtils.h"
#include "../global/ShineSetting.h"
#include "../support/SMath.h"

/** 字节读流 */
class BytesReadStream
{
private:
	char* _buf = nullptr;
	int _bufSize = 0;
	/** 是否自己new的buf */
	bool _selfNewBuf = false;

	int _length = 0;
	int _position = 0;

	/** 读取长度限制(防溢出) */
	int _readLenLimit = 0;
	/** 读取次数 */
	int _readNum = 0;
	/** 读锁,只为加速运算 */
	bool _readLock = false;


public:
	BytesReadStream();
	~BytesReadStream();
	BytesReadStream(int size);

	BytesReadStream(char* buf, int length)
	{
		setBuf(buf, length, length);
	}

	BytesReadStream(char* buf, int bufSize, int length)
	{
		setBuf(buf, bufSize, length);
	}

	void setBuf(char* buf, int length)
	{
		setBuf(buf, length, length);
	}

	void setBuf(char* buf, int bufSize, int length);

	/** 获取获取字节数组 */
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

	/** 抛错 */
	void throwError(string str)
	{
		Ctrl::throwError(str);
	}

	/** 抛出类型转发错误 */
	void throwTypeReadError(const type_info& cls1, const type_info& cls2)
	{
		throwError(string("读取时，类型升级失败") + cls1.name() + " " + cls2.name());
	}

	/** 设置长度限制(可缩短) */
	void setLength(int len);

	/** 剩余可读数目 */
	int bytesAvailable()
	{
		return _length - _position;
	}

	/** 是否已为空(没有剩余字节) */
	bool isEmpty()
	{
		return (_length - _position) == 0;
	}

	/** 获取字节 */
	char getByte(int index)
	{
		return _buf[index];
	}

	/** 得到字节缓存的字节数组(副本)(从0到length) */
	char* getByteArray();

	/** 得到字节缓存的字节数组(副本)(从position 0到length) */
	char* getByteArrayFromPos();

	/** 清空(不清off) */
	void clear();

	//--read--//


	/** 读出字节组(字节读写用) */
	bool readByteArr(char* bytes, int len);

	/** 读出字节组 */
	char* readByteArr(int len);

	/** 读出一个布尔值 */
	bool readBoolean();

	/** 读出一个字节 */
	int readByte();

	/** 读出一个无符号字节 */
	int readUnsignedByte();

	/** 读出一个短整型数值 */
	int readShort();

	/** 原版读short */
	int natureReadShort();

	/** 读ushort */
	int readUnsignedShort();

	/** 原版读ushort */
	int natureReadUnsignedShort();

	/** 读出一个整型数值 */
	int readInt();

	/** 原版读int */
	int natureReadInt();

	/** 读出一个浮点数 */
	float readFloat();

	/** 读出一个双精数 */
	double readDouble();

	/** 读出一个长整型数值 */
	int64 readLong();

	/** 原版读long */
	int64 natureReadLong();

	/** 读出指定长的字符串 */
	string readUTFBytes(int length);

	/** 读出一个字符串，在最前用一UnsignedShort表示字符长度 */
	string readString()
	{
		return readUTF();
	}

	/** 读出一个字符串，在最前用一UnsignedShort表示字符长度 */
	string readUTF()
	{
		if (_readLock)
		{
			return "";
		}

		return readUTFBytes(readLen());
	}

	/** 读一个长度 */
	int readLen();

	/** 开始读对象 */
	int startReadObj();

	/** 结束读对象 */
	void endReadObj(int len);

	/** 设置读取长度限制 */
	void setReadLenLimit(int value)
	{
		_readLenLimit = value;
	}

	/** 读取一定长度内存到指定指针 */
	void readMem(void* ptr, int len);

	//--data--//

	///** 读出一个非空数据(完整版) */
	//BaseData* readDataFullNotNull()
	//{
	//	if (isEmpty())
	//		return nullptr;

	//	int position = startReadObj();

	//	int dataID = readShort();

	//	BaseData* data;

	//	if (dataID <= 0)
	//	{
	//		Ctrl::log("不该找不到dataID");
	//		data = nullptr;
	//	}
	//	else
	//	{
	//		data = BytesControl::getDataByID(dataID);

	//		if (data != nullptr)
	//		{
	//			data->readBytesFullWithoutLen(this);
	//		}
	//	}

	//	endReadObj(position);

	//	return data;
	//}

	///** 读取数据(考虑空)(可继承的)(完整版) */
	//public BaseData readDataFull()
	//{
	//	if (this.readBoolean())
	//	{
	//		return readDataFullNotNull();
	//	}
	//	else
	//	{
	//		return null;
	//	}
	//}

	///** 读出一个非空数据(可继承的)(简版) */
	//public BaseData readDataSimpleNotNull()
	//{
	//	if (isEmpty())
	//		return null;

	//	int dataID = this.readShort();

	//	BaseData data;

	//	if (dataID <= 0)
	//	{
	//		Ctrl.log("不该找不到dataID");
	//		data = null;
	//	}
	//	else
	//	{
	//		data = BytesControl.getDataByID(dataID);

	//		if (data != null)
	//		{
	//			data.readBytesSimple(this);
	//		}
	//	}

	//	return data;
	//}

	///** 读取数据(考虑空)(简版) */
	//public BaseData readDataSimple()
	//{
	//	if (this.readBoolean())
	//	{
	//		return readDataSimpleNotNull();
	//	}
	//	else
	//	{
	//		return null;
	//	}
	//}

	//check

	///** 获取某一段的字节hash(short) */
	//public short getHashCheck(int pos, int length)
	//{
	//	return BytesUtils.getHashCheck(_buf, pos + _off, length);
	//}

	////uncompress

	///** 解压缩(0-length)无视position */
	//public void unCompress()
	//{
	//	byte[] b = BytesUtils.uncompressByteArr(_buf, _off, _length);

	//	_buf = b;
	//	_off = 0;
	//	_position = 0;
	//	_length = b.length;
	//}

	/** 克隆一个 */
	BytesReadStream* clone()
	{
		BytesReadStream* re = new BytesReadStream();
		re->_buf = _buf;
		re->_position = _position;
		re->_length = _length;

		return re;
	}

	/** 转化为string */
	string toString()
	{
		return BytesUtils::bytesToString(_buf, 0, _length);
	}

	/** 检查版本 */
	bool checkVersion(int version);

	Vector3 readVector3();

private:
	/** 遇到文件尾 */
	void tailError();

	bool ensureCanRead(int len);
};
