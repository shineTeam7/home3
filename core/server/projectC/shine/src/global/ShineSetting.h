#pragma once
#include "../SInclude.h"

/** shine配置 */
class ShineSetting
{
public:
	/** 是否需要抛错 */
	static bool needError;
	/** 是否需要字节读写超限的时候报错 */
	static bool needBytesLenLimitError;
};
