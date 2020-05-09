#pragma once
//#include <iostream>
//#include <string>
//using namespace std;

#include <stdarg.h>
#include "../SInclude.h"

using namespace std;

void print(char const* str, ...);


/** 控制类 */
class Ctrl
{
public:
	/** 输出 */
	static void print(string str, ...);
	/** 日志 */
	static void log(string str, ...);
	/** 抛错 */
	static void throwError(string str, ...);
	/** 错误日志 */
	static void errorLog(string str, ...);
	/** 获取当前毫秒数 */
	static int64 getTimer();
	/** 获取当前毫微数 */
	static int64 getNanoTimer();
private:

};
