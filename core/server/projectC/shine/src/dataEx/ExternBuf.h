#pragma once
#include "../SInclude.h"
#include "../extern/ExternHead.h"

/** 外部buf */
class ExternBuf
{
private:
	int _fIndex = 0;
	int _iIndex = 0;
	int _lIndex = 0;

public:
	float* fArr;
	jint* iArr;
	int64* lArr;

	ExternBuf(float* fArr, jint* iArr, int64* lArr);
	~ExternBuf();

	void clear();

	void writeFloat(float v);

	void writeInt(int v);

	void writeLong(int64 v);

	void writeBoolean(bool v);

	float readFloat();

	int readInt();

	int64 readLong();

	bool readBoolean();

	void readVector(float* arr);

	void writeVector(float* arr);
};
