#pragma once
/** 对unity导出接口 */
#include "SInclude.h"
#include "extern/ExternHead.h"

#ifndef __linux__

extern "C" {
	/** 暂时弃用 */
	JNIEXPORT bool exportNavMeshTiles(char* configBuf, int configLength, char* dataBuf, int dataSize, const char* savePath);

}

#endif