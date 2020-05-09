#pragma once
#include "../SInclude.h"

/** 对象方法 */
class ObjectUtils
{
public:
	/** float转int编码 */
	inline static int encodeFloat(float value)
	{
		union
		{
			float f;
			int i;
		};
		f = value;

		return i;
	}

	/** int转float编码 */
	inline static float decodeFloat(int value)
	{
		union
		{
			float f;
			int i;
		};
		i = value;

		return f;
	}

	/** double转long编码 */
	inline static int64 encodeDouble(double value)
	{
		union
		{
			double d;
			int64 l;
		};
		d = value;
		return l;
	}

	/** long转double编码 */
	inline static double decodeDouble(int64 value)
	{
		union
		{
			double d;
			int64 l;
		};
		l = value;
		return d;
	}
};
