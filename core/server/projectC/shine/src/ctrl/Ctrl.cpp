#include "Ctrl.h"
#include "../support/PerfTimer.h"

void print(const char* str, ...)
{
	va_list va;
	va_start(va, str);
	vprintf(str, va);
	va_end(va);
	printf("\n");
}

void Ctrl::print(string str, ...)
{
	va_list va;
	va_start(va, str);
	vprintf(str.c_str(), va);
	va_end(va);
	printf("\n");
}

void Ctrl::log(string str, ...)
{
	va_list va;
	va_start(va, str);
	vprintf(str.c_str(), va);
	va_end(va);
	printf("\n");
}

void Ctrl::throwError(string str, ...)
{
	va_list va;
	va_start(va, str);
	vprintf(str.c_str(), va);
	va_end(va);
	printf("\n");
}

void Ctrl::errorLog(string str, ...)
{
	va_list va;
	va_start(va, str);
	vprintf(str.c_str(), va);
	va_end(va);
	printf("\n");


}

int64 Ctrl::getTimer()
{
	return getPerfTime() / 1000;
}

int64 Ctrl::getNanoTimer()
{
	return getPerfTime();
}
