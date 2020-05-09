#include "ExternBuf.h"

ExternBuf::ExternBuf(float* fArr, jint* iArr, int64* lArr)
{
	this->fArr = fArr;
	this->iArr = iArr;
	this->lArr = lArr;
}

ExternBuf::~ExternBuf()
{
	this->fArr = nullptr;
	this->iArr = nullptr;
	this->lArr = nullptr;
}

void ExternBuf::clear()
{
	_fIndex = 0;
	_iIndex = 0;
	_lIndex = 0;
}

void ExternBuf::writeFloat(float v)
{
	fArr[_fIndex++] = v;
}

void ExternBuf::writeInt(int v)
{
	iArr[_iIndex++] = v;
}

float ExternBuf::readFloat()
{
	return fArr[_fIndex++];
}

void ExternBuf::writeLong(int64 v)
{
	lArr[_lIndex++] = v;
}

int ExternBuf::readInt()
{
	return iArr[_iIndex++];
}

int64 ExternBuf::readLong()
{
	return lArr[_lIndex++];
}

void ExternBuf::readVector(float* arr)
{
	arr[0] = readFloat();
	arr[1] = readFloat();
	arr[2] = readFloat();
}

void ExternBuf::writeVector(float* arr)
{
	writeFloat(arr[0]);
	writeFloat(arr[1]);
	writeFloat(arr[2]);
}

void ExternBuf::writeBoolean(bool v)
{
	writeInt(v ? 1 : 0);
}

bool ExternBuf::readBoolean()
{
	return readInt() == 1;
}
