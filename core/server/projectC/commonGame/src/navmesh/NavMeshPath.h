#pragma once
#include "SInclude.h"
#include "dataEx/ExternBuf.h"

static const int MaxPathNum = 100;

class NavMeshPath
{
public:
	float path[3 * MaxPathNum];
	int pointNum = 0;

	void writeBuf(ExternBuf* buf)
	{
		//不写起始点

		buf->writeInt(pointNum - 1);

		int len = pointNum * 3;

		for (int i = 3; i < len; ++i)
		{
			buf->writeFloat(path[i]);
		}
	}
};
