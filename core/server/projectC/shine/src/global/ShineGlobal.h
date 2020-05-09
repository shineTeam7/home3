#pragma once
#include "../SInclude.h"

class ShineGlobal
{
public:
	/** bin目录 */
	static string binPath;
	/** 初始化 */
	static void init(string binPathV);
private:
	static bool _inited;
};
