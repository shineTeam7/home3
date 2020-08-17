#pragma once
#include "../SInclude.h"
#include <math.h>


class MathUtils
{
	static const float epsilon;
public:
	/** 获取该数字的2次幂(re>=n) */
	static int getPowerOf2(int n)
	{
		if ((n & (n - 1)) == 0)
		{
			return n;
		}

		return UINT32_MAX >> (numberOfLeadingZeros(n) - 1);
	}

	static uint numberOfLeadingZeros(uint i)
	{
		// HD, Figure 5-6
		if (i == 0u)
			return 32u;

		uint n = 1u;
		if (i >> 16 == 0u)
		{
			n += 16u;
			i <<= 16;
		}
		if (i >> 24 == 0u)
		{
			n += 8u;
			i <<= 8;
		}
		if (i >> 28 == 0u)
		{
			n += 4u;
			i <<= 4;
		}
		if (i >> 30 == 0u)
		{
			n += 2u;
			i <<= 2;
		}
		n -= i >> 31;
		return n;
	}

	inline static float clampf(float value, float min, float max)
	{
		if (value < min)
			return min;

		if (value > max)
			return max;

		return value;
	}

	inline static bool floatEquals(float v0, float v1)
	{
		return fabs(v0 - v1) < epsilon;
	}

	inline static float lerpf(float from, float to, float t)
	{
		return to * t + from * (1.0f - t);
	}

private:

};
