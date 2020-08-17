#pragma once
#include <stdint.h>

typedef unsigned int uint;

typedef int8_t int8;
typedef int16_t int16;
typedef int32_t int32;
typedef int64_t int64;

typedef uint8_t uint8;
typedef uint16_t uint16;
typedef uint32_t uint32;
typedef uint64_t uint64;

#ifdef _WIN32
#define _isWindows
#endif

#ifdef __APPLE__
#define _isMac
#endif

#ifdef __linux__
#define _isLinux
#endif

#define ZeroSet(Cls) memset(this,0,sizeof(Cls));
