#include "BytesReadStream.h"
#include<cstring>

BytesReadStream::BytesReadStream()
{
	_selfNewBuf = false;
}

BytesReadStream::BytesReadStream(int size)
{
	setBuf(new char[size], size, size);
	_selfNewBuf = true;
}

BytesReadStream::~BytesReadStream()
{
	if (_selfNewBuf)
	{
		delete[] _buf;
	}

	_buf = nullptr;
}

void BytesReadStream::setBuf(char* buf, int bufSize, int length)
{
	if (_selfNewBuf)
	{
		delete[] _buf;
		_buf = nullptr;
		_selfNewBuf = false;
	}

	_buf = buf;
	_bufSize = bufSize;
	_length = length;

	_position = 0;
	_readNum = 0;
	_readLock = false;
	_readLenLimit = 0;
}

void BytesReadStream::setPosition(int pos)
{
	if (pos < 0)
	{
		throwError("位置不能小于0");
		pos = 0;
	}

	if (pos > _length)
	{
		tailError();
		pos = _length;
	}

	_position = pos;
}

void BytesReadStream::setLength(int len)
{
	if (len > _bufSize)
	{
		tailError();
	}

	_length = len;
}

char* BytesReadStream::getByteArray()
{
	char* re = new char[_length];
	memcpy(re, _buf, _length);

	return re;
}

char* BytesReadStream::getByteArrayFromPos()
{
	int len = _length - _position;
	char* re = new char[len];
	memcpy(re, _buf + _position, len);

	return re;
}

void BytesReadStream::clear()
{
	_position = 0;
	_length = 0;
	_readNum = 0;
	_readLock = false;
	_readLenLimit = 0;
}

bool BytesReadStream::readByteArr(char* bytes, int len)
{
	if (!ensureCanRead(len))
		return false;

	memcpy(bytes, _buf + _position, len);

	_position += len;

	return true;
}

char* BytesReadStream::readByteArr(int len)
{
	if (!ensureCanRead(len))
	{
		return nullptr;
	}

	char* re = new char[len];

	memcpy(re, _buf + _position, len);

	_position += len;

	return re;
}

bool BytesReadStream::readBoolean()
{
	if (!ensureCanRead(1))
	{
		return false;
	}

	return _buf[_position++] != 0;
}

int BytesReadStream::readByte()
{
	if (!ensureCanRead(1))
	{
		return 0;
	}

	return _buf[_position++];
}

int BytesReadStream::readUnsignedByte()
{
	if (!ensureCanRead(1))
	{
		return 0;
	}

	return _buf[_position++] & 0xff;
}

int BytesReadStream::readShort()
{
	if (!ensureCanRead(1))
	{
		return 0;
	}

	int v = _buf[_position];

	int sign = v & 0x80;

	int n = v & 0x7f;

	int re;

	if (n >= 0x40)
	{
		++_position;
		re = n & 0x3f;
	}
	else if (n >= 0x20)
	{
		re = natureReadShort() & 0x1fff;
	}
	else if (n >= 0x10)
	{
		++_position;
		re = natureReadShort();
	}
	else
	{
		throwError("readShort,invalid number:" + to_string(n));
		re = 0;
	}

	return sign > 0 ? -re : re;
}

int BytesReadStream::natureReadShort()
{
	if (!ensureCanRead(2))
	{
		return 0;
	}

	int pos = _position;
	_position += 2;

	return (short)((_buf[pos + 1] & 0xff) | ((_buf[pos] & 0xff) << 8));
}

int BytesReadStream::readUnsignedShort()
{
	if (!ensureCanRead(1))
	{
		return 0;
	}

	int n = _buf[_position] & 0xff;

	int re;

	if (n >= 0x80)
	{
		++_position;
		re = n & 0x7f;
	}
	else if (n >= 0x40)
	{
		re = natureReadUnsignedShort() & 0x3fff;
	}
	else if (n >= 0x20)
	{
		++_position;
		re = natureReadUnsignedShort();
	}
	else
	{
		throwError("readUnsignedShort,invalid number:" + to_string(n));
		re = 0;
	}

	return re;
}

int BytesReadStream::natureReadUnsignedShort()
{
	if (!ensureCanRead(2))
	{
		return 0;
	}

	int pos = _position;
	_position += 2;

	return (_buf[pos + 1] & 0xff) | ((_buf[pos] & 0xff) << 8);
}

int BytesReadStream::readInt()
{
	if (!ensureCanRead(1))
	{
		return 0;
	}

	int v = _buf[_position];

	int sign = v & 0x80;

	int n = v & 0x7f;

	int re;

	if (n >= 0x40)
	{
		++_position;
		re = n & 0x3f;
	}
	else if (n >= 0x20)
	{
		re = natureReadShort() & 0x1fff;
	}
	else if (n >= 0x10)
	{
		re = natureReadInt() & 0x0fffffff;
	}
	else if (n >= 8)
	{
		++_position;
		re = natureReadInt();
	}
	else
	{
		throwError("readInt,invalid number:" + to_string(n));
		re = 0;
	}

	return sign > 0 ? -re : re;
}

int BytesReadStream::natureReadInt()
{
	if (!ensureCanRead(4))
	{
		return 0;
	}

	int pos = _position;

	_position += 4;

	char* buf = _buf;

	return (buf[pos + 3] & 0xff) | ((buf[pos + 2] & 0xff) << 8) | ((buf[pos + 1] & 0xff) << 16) | ((buf[pos] & 0xff) << 24);
}

float BytesReadStream::readFloat()
{
	if (_readLock)
	{
		return 0.0f;
	}

	return ObjectUtils::decodeFloat(natureReadInt());
}

double BytesReadStream::readDouble()
{
	if (_readLock)
	{
		return 0.0;
	}

	return ObjectUtils::decodeDouble(natureReadLong());
}

int64 BytesReadStream::readLong()
{
	if (!ensureCanRead(1))
	{
		return 0L;
	}

	int v = _buf[_position];

	int sign = v & 0x80;

	int n = v & 0x7f;

	int64 re;

	if (n >= 0x40)
	{
		++_position;
		re = n & 0x3f;
	}
	else if (n >= 0x20)
	{
		re = natureReadShort() & 0x1fff;
	}
	else if (n >= 0x10)
	{
		re = natureReadInt() & 0x0fffffff;
	}
	else if (n >= 8)
	{
		++_position;
		re = natureReadInt();
	}
	else if (n >= 4)
	{
		++_position;
		re = natureReadLong();
	}
	else
	{
		throwError("readLong,invalid number:" + to_string(n));
		re = 0;
	}

	return sign > 0 ? -re : re;
}

int64 BytesReadStream::natureReadLong()
{
	if (!ensureCanRead(8))
	{
		return 0L;
	}

	int pos = _position;
	_position += 8;

	char* buf = _buf;

	int64 re = (buf[pos + 7] & (int64)0xffL);
	re |= ((buf[pos + 6] & (int64)0xffL) << 8);
	re |= ((buf[pos + 5] & (int64)0xffL) << 16);
	re |= ((buf[pos + 4] & (int64)0xffL) << 24);
	re |= ((buf[pos + 3] & (int64)0xffL) << 32);
	re |= ((buf[pos + 2] & (int64)0xffL) << 40);
	re |= ((buf[pos] & (int64)0xffL) << 56);
	return re;
	//return (buf[pos + 7] & (int64)0xffL) | ((buf[pos + 6] & (int64)0xffL) << 8) | ((buf[pos + 5] & (int64)0xffL) << 16) | ((buf[pos + 4] & (int64)0xffL) << 24) | ((buf[pos + 3] & (int64)0xffL) << 32) | ((buf[pos + 2] & (int64)0xffL) << 40) | ((buf[pos + 1] & (int64)0xffL) << 48) | ((buf[pos] & (int64)0xffL) << 56);
}

std::string BytesReadStream::readUTFBytes(int length)
{
	if (!ensureCanRead(length))
	{
		return "";
	}

	if (length == 0)
	{
		return "";
	}

	int pos = _position;

	_position += length;

	string re;
	re.resize(length);
	char* p = &(re[0]);
	memcpy(p, _buf + pos, length);

	return re;
}

int BytesReadStream::readLen()
{
	//这里不走ensure
	if (_readLock)
	{
		return 0;
	}

	if (_position + 1 > _length)
	{
		return 0;
	}

	int re;
	int n = _buf[_position] & 0xff;

	if (n >= 0x80)
	{
		++_position;
		re = n & 0x7f;
	}
	else if (n >= 0x40)
	{
		if (_position + 2 > _length)
		{
			return 0;
		}

		re = natureReadUnsignedShort() & 0x3fff;
	}
	else if (n >= 0x20)
	{
		if (_position + 4 > _length)
		{
			return 0;
		}

		re = natureReadInt() & 0x1fffffff;
	}
	else if (n >= 0x10)
	{
		if (_position + 5 > _length)
		{
			return 0;
		}

		++_position;

		re = natureReadInt();
	}
	else
	{
		throwError("readLen,invalid number:" + to_string(n));
		re = 0;
	}

	if (_readLenLimit > 0 && re >= _readLenLimit)
	{
		if (ShineSetting::needBytesLenLimitError)
			Ctrl::errorLog("readLen,超过长度限制:" + re);
	}

	return re;
}

int BytesReadStream::startReadObj()
{
	int len = readLen();
	int pos = _position + len;

	int re = _length;
	_readNum++;
	_length = pos;

	return re;
}

void BytesReadStream::endReadObj(int len)
{
	if (_readNum == 0)
	{
		throwError("不该出现的");
		return;
	}

	setPosition(_length);

	_length = len;

	_readNum--;
	_readLock = false;
}

void BytesReadStream::readMem(void* ptr, int len)
{
	if (!ensureCanRead(len))
		return;

	memcpy(ptr, _buf + _position, len);
	_position += len;
}

bool BytesReadStream::checkVersion(int version)
{
	if (bytesAvailable() < 4)
		return false;

	return natureReadInt() == version;
}

void BytesReadStream::tailError()
{
	int pos = _position;

	setPosition(_length);

	if (_readNum > 0)
	{
		_readLock = true;
	}
	else
	{
		throwError("遇到文件尾,pos:" + to_string(pos) + " length:" + to_string(_length));
	}
}

bool BytesReadStream::ensureCanRead(int len)
{
	if (_readLock)
		return false;

	if (_position + len > _length)
	{
		tailError();
		return false;
	}

	return true;
}

Vector3 BytesReadStream::readVector3()
{
	Vector3 re;
	re.x = readFloat();
	re.y = readFloat();
	re.z = readFloat();
	return re;
}
