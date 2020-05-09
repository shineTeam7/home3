#pragma once
#include "../SInclude.h"

static const char _hexArr[16] = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

class BytesUtils
{
public:
	/** 二进制转string */
	static string bytesToString(char* buf, int off, int length)
	{
		string re;
		re.resize(length << 1);
		char* p = &(re[0]);

		char c;

		for (int i = off; i < length; ++i)
		{
			c = buf[i];
			p[i << 1] = _hexArr[c >> 8];
			p[(i << 1) + 1] = _hexArr[c & 0xf];
		}

		return re;
	}

private:

};
