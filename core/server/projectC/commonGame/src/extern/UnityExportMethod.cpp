#include "UnityExportMethod.h"
#include "bytes/BytesReadStream.h"
#include "../navmesh/NavMesh.h"

#ifndef __linux__

bool exportNavMeshTiles(char* configBuf, int configLength, char* dataBuf, int dataSize, const char* savePath)
{
	if (!configBuf)
		return false;

	BytesReadStream* configStream = new BytesReadStream(configBuf, configLength);
	BytesReadStream* dataStream = new BytesReadStream(dataBuf, dataSize);

	NavMesh ep;
	ep.exportNav(configStream, dataStream, savePath);

	delete configStream;
	delete dataStream;

	return true;
}


#endif
