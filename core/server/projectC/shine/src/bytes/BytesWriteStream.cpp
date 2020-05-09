#include "BytesWriteStream.h"


BytesWriteStream::BytesWriteStream(int size)
{
	setBuf(new char[size], size, size);
	_selfNewBuf = true;
	_canGrow = true;
}

BytesWriteStream::~BytesWriteStream()
{
	if (_selfNewBuf)
	{
		delete[] _buf;
	}

	_buf = nullptr;
}

void BytesWriteStream::setBuf(char* buf, int bufSize, int length)
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
	_writeLenLimit = 0;

	_canGrow = false;
}

void BytesWriteStream::setPosition(int pos)
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

void BytesWriteStream::setLength(int len)
{
	if (len <= _length)
	{
		return;
	}

	//超过了再grow
	if (len > _bufSize)
	{
		grow(len);
	}

	_length = len;
}

void BytesWriteStream::tailError()
{
	int pos = _position;

	setPosition(_length);

	throwError("遇到文件尾,pos:" + to_string(pos) + " length:" + to_string(_length));
}

void BytesWriteStream::writeByteArr(char* bbs, int off, int length)
{
	if (!ensureCanWrite(length))
	{
		return;
	}

	memcpy(_buf + _position, bbs + off, length);

	_position += length;
}

void BytesWriteStream::insertVoidBytes(int num, int length)
{
	if (!ensureCanWrite(num + length))
	{
		return;
	}

	memmove(_buf + _position + num, _buf + _position, length);
}

void BytesWriteStream::writeBoolean(bool value)
{
	if (!ensureCanWrite(1))
	{
		return;
	}

	_buf[_position++] = value ? 1 : 0;
}

void BytesWriteStream::writeByte(int value)
{
	if (!ensureCanWrite(1))
	{
		return;
	}

	_buf[_position++] = value;
}

void BytesWriteStream::writeUnsignedByte(int value)
{
	if (!ensureCanWrite(1))
	{
		return;
	}

	_buf[_position++] = value;
}

void BytesWriteStream::writeShort(int value)
{
	int sign = value < 0 ? 0x80 : 0;
	int v = value >= 0 ? value : -value;

	if (v < 0x40)
	{
		writeUnsignedByte((sign | 0x40) | v);
	}
	else if (v < 0x2000)
	{
		natureWriteShort((sign << 8 | 0x2000) | v);
	}
	else
	{
		writeUnsignedByte(sign | 0x10);
		natureWriteShort(v);
	}
}

void BytesWriteStream::natureWriteShort(int value)
{
	if (!ensureCanWrite(2))
	{
		return;
	}

	int pos = _position;

	char* buf = _buf;

	buf[pos] = (value >> 8);
	buf[pos + 1] = value;

	_position += 2;
}

void BytesWriteStream::writeUnsignedShort(int value)
{
	if (value < 0x80)
	{
		writeUnsignedByte(value | 0x80);
	}
	else if (value < 0x4000)
	{
		natureWriteUnsignedShort(value | 0x4000);
	}
	else
	{
		writeUnsignedByte(0x20);
		natureWriteUnsignedShort(value);
	}
}

void BytesWriteStream::natureWriteUnsignedShort(int value)
{
	if (!ensureCanWrite(2))
	{
		return;
	}

	int pos = _position;

	char* buf = _buf;

	buf[pos] = (value >> 8);
	buf[pos + 1] = value;

	_position += 2;
}

void BytesWriteStream::writeInt(int value)
{
	int sign = value < 0 ? 0x80 : 0;
	int v = value >= 0 ? value : -value;

	if (v < 0x40)
	{
		writeUnsignedByte((sign | 0x40) | v);
	}
	else if (v < 0x2000)
	{
		natureWriteShort((sign << 8 | 0x2000) | v);
	}
	else if (v < 0x10000000)
	{
		natureWriteInt((sign << 24 | 0x10000000) | v);
	}
	else
	{
		writeUnsignedByte(sign | 8);
		natureWriteInt(v);
	}
}

void BytesWriteStream::natureWriteInt(int value)
{
	if (!ensureCanWrite(4))
	{
		return;
	}

	int pos = _position;

	char* buf = _buf;

	buf[pos] = (value >> 24);
	buf[pos + 1] = (value >> 16);
	buf[pos + 2] = (value >> 8);
	buf[pos + 3] = (value);

	_position += 4;
}

void BytesWriteStream::writeFloat(float value)
{
	natureWriteInt(ObjectUtils::encodeFloat(value));
}

void BytesWriteStream::writeDouble(double value)
{
	natureWriteLong(ObjectUtils::encodeDouble(value));
}

void BytesWriteStream::writeLong(int64 value)
{
	int sign = value < 0 ? 0x80 : 0;
	int64 v = value >= 0 ? value : -value;

	if (v < 0x40)
	{
		writeUnsignedByte((sign | 0x40) | (int)v);
	}
	else if (v < 0x2000)
	{
		natureWriteShort((sign << 8 | 0x2000) | (int)v);
	}
	else if (v < 0x10000000)
	{
		natureWriteInt((sign << 24 | 0x10000000) | (int)v);
	}
	else if (v < INT32_MAX)
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

void BytesWriteStream::natureWriteLong(int64 value)
{
	if (!ensureCanWrite(8))
	{
		return;
	}

	int pos = _position;

	char* buf = _buf;

	buf[pos] = (value >> 56);
	buf[pos + 1] = (value >> 48);
	buf[pos + 2] = (value >> 40);
	buf[pos + 3] = (value >> 32);
	buf[pos + 4] = (value >> 24);
	buf[pos + 5] = (value >> 16);
	buf[pos + 6] = (value >> 8);
	buf[pos + 7] = (value);

	_position += 8;
}

void BytesWriteStream::writeUTFBytes(const string& str)
{
	/*if (str == nullptr)
	{
		throwError("字符串不能为空");
		str = "";
	}*/

	int size = str.size();

	writeByteArr((char*)str.data(), size);
}

void BytesWriteStream::writeUTF(const string& str)
{
	/*if (str == nullptr)
	{
		throwError("字符串不能为空");
		str = "";
	}*/

	int size = str.size();

	writeLen(size);
	writeByteArr((char*)str.data(), size);
}

void BytesWriteStream::writeLen(int value)
{
	if (_writeLenLimit > 0 && value >= _writeLenLimit)
	{
		if (ShineSetting::needBytesLenLimitError)
			Ctrl::errorLog("writeLen,超过长度限制:" + value);
	}

	if (value < 0)
	{
		writeByte(0);
	}
	else if (value < 0x80)
	{
		writeUnsignedByte(value | 0x80);
	}
	else if (value < 0x4000)
	{
		natureWriteShort(value | 0x4000);
	}
	else if (value < 0x20000000)
	{
		natureWriteInt(value | 0x20000000);
	}
	else
	{
		writeByte(0x10);
		natureWriteInt(value);
	}
}

int BytesWriteStream::getLenSize(int value)
{
	if (value < 0)
	{
		Ctrl::throwError("长度不能小于0" + value);
		return 1;
	}
	else if (value < 0x80)
	{
		return 1;
	}
	else if (value < 0x4000)
	{
		return 2;
	}
	else if (value < 0x20000000)
	{
		return 4;
	}
	else
	{
		return 5;
	}
}

int BytesWriteStream::startWriteObj()
{
	return _position;
}

void BytesWriteStream::endWriteObj(int position)
{
	insertLenToPos(position);
}

void BytesWriteStream::writeBytesStream(BytesWriteStream stream, int pos, int length)
{
	if (stream.length() < pos + length)
	{
		tailError();
		return;
	}

	writeByteArr(stream._buf, pos, length);
}

void BytesWriteStream::insertLenToPos(int pos, int len)
{
	int cp = getPosition();

	setPosition(pos);

	int size = getLenSize(len);

	insertVoidBytes(size, cp - pos);

	writeLen(len);

	setPosition(cp + size);
}

void BytesWriteStream::grow(int len)
{
	int cap;

	if ((cap = MathUtils::getPowerOf2(len)) > _bufSize)
	{
		char* bs = new char[cap];

		memcpy(bs, _buf, _length);

		delete[] _buf;

		_buf = bs;
		_bufSize = cap;
	}
}

bool BytesWriteStream::ensureCanWrite(int len)
{
	if (_position + len > _length)
	{
		if (_canGrow)
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

void BytesWriteStream::writeVector3(Vector3& vec)
{
	writeFloat(vec.x);
	writeFloat(vec.y);
	writeFloat(vec.z);
}
