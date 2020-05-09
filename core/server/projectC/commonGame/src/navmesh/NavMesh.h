#pragma once

#include "SInclude.h"
#include "bytes/BytesReadStream.h"
#include "support/SMath.h"
#include "support/collection/SList.h"
#include <Recast.h>
#include <DetourTileCache.h>
#include <DetourTileCacheBuilder.h>
#include <DetourCommon.h>
#include "contrib/fastlz/fastlz.h"
#include <DetourNavMesh.h>
#include <DetourNavMeshQuery.h>
#include <DetourNavMeshBuilder.h>
#include "NavMeshHit.h"
#include "NavMeshPath.h"

static const int EXPECTED_LAYERS_PER_TILE = 4;
static const int MAX_LAYERS = 32;

static const int TILECACHESET_MAGIC = 'T' << 24 | 'S' << 16 | 'E' << 8 | 'T'; //'TSET';
static const int TILECACHESET_VERSION = 1;

static const int RC_FORCE_UNWALKABLE_AREA = 0xff;

static const int kNavMeshVertsPerPoly = 6;

static const int kWalkable = 0;
static const int kNotWalkable = 1;
static const int kJump = 2;

struct TileCacheSetHeader
{
	int magic;
	int version;
	int numTiles;
	dtNavMeshParams meshParams;
	dtTileCacheParams cacheParams;
};

struct TileCacheData
{
	unsigned char* data = nullptr;
	int dataSize = 0;
};

struct FastLZCompressor : public dtTileCacheCompressor
{
	virtual int maxCompressedSize(const int bufferSize)
	{
		return (int)(bufferSize * 1.05f);
	}

	dtStatus compress(const unsigned char* buffer, const int bufferSize,
		unsigned char* compressed, const int /*maxCompressedSize*/, int* compressedSize)
	{
		*compressedSize = fastlz_compress((const void* const)buffer, bufferSize, compressed);
		return DT_SUCCESS;
	}

	dtStatus decompress(const unsigned char* compressed, const int compressedSize,
		unsigned char* buffer, const int maxBufferSize, int* bufferSize)
	{
		*bufferSize = fastlz_decompress(compressed, compressedSize, buffer, maxBufferSize);
		return *bufferSize < 0 ? DT_FAILURE : DT_SUCCESS;
	}
};

struct LinearAllocator : public dtTileCacheAlloc
{
	unsigned char* buffer;
	size_t capacity;
	size_t top;
	size_t high;

	LinearAllocator(const size_t cap) : buffer(0), capacity(0), top(0), high(0)
	{
		resize(cap);
	}

	~LinearAllocator()
	{
		dtFree(buffer);
	}

	void resize(const size_t cap)
	{
		if (buffer) dtFree(buffer);
		buffer = (unsigned char*)dtAlloc(cap, DT_ALLOC_PERM);
		capacity = cap;
	}

	virtual void reset()
	{
		high = dtMax(high, top);
		top = 0;
	}

	virtual void* alloc(const size_t size)
	{
		if (!buffer)
			return 0;
		if (top + size > capacity)
			return 0;
		unsigned char* mem = &buffer[top];
		top += size;
		return mem;
	}

	virtual void free(void* /*ptr*/)
	{
		// Empty
	}
};

class BuildContext : public rcContext
{
public:

protected:

	virtual void doLog(const rcLogCategory category, const char* msg, const int len);
};

struct MeshProcess : public dtTileCacheMeshProcess
{
public:
	virtual void process(struct dtNavMeshCreateParams* params, unsigned char* polyAreas, unsigned short* polyFlags)override;
};

class NavMeshConfig
{
public:
	float agentHeight = 2.0f;
	float agentRadius = 0.5f;
	float agentMaxClimb = 0.75f;
	float agentMaxSlope = 45.0f;

	float tileSize = 64;
	float cellSize = 0.5f;

	float regionMinSize = 8;
	float edgeMaxError = 1.3f;

	void read(BytesReadStream* stream);
};

class SceneRecastTerrainData
{
public:
	~SceneRecastTerrainData();

	Vector3 origin;
	Vector3 unit;

	int resolutionX;
	int resolutionZ;

	unsigned short* heightSamples;

	void read(BytesReadStream* stream);

	void getVertex(int x, int z, float* out);
};

class SceneRecastMeshData
{
public:
	~SceneRecastMeshData();
	int* triangles;
	int trianglesSize;

	Vector3* vertices;
	int verticesSize;

	Vector3 min;
	Vector3 max;

	void read(BytesReadStream* stream);
};

class SceneRecastObjData
{
public:
	int meshIndex;
	int area;
	//	Vector3 x;
	//	Vector3 y;
	//	Vector3 z;
	//	Vector3 w;
	//	Vector3 min;
	//	Vector3 max;

	float x[3];
	float y[3];
	float z[3];
	float w[3];
	float min[3];
	float max[3];

	void read(BytesReadStream* stream);

	void transVert(float* src, float* rtn);
};

class SceneRecastData
{
public:
	~SceneRecastData();

	int originX;
	int originZ;
	int sizeX;
	int sizeZ;
	float tileSize;

	Vector3 min;
	Vector3 max;

	SceneRecastTerrainData* terrain;

	SceneRecastMeshData** meshList;
	int meshListSize;

	SceneRecastObjData** objList;
	int objListSize;

	void read(BytesReadStream* stream);
};

class NavMesh
{
public:

	NavMesh()
	{

	}

	~NavMesh();

	//export
	void exportNav(const char* configPath, const char* dataPath, const char* savePath);
	void exportNav(BytesReadStream* configStream, BytesReadStream* dataStream, string savePath);
	void save(string path);

	//runtime
	void readBytes(char* bytes, int len);

	bool samplePosition(NavMeshHit* hit, const float* position, const dtQueryFilter& filter, float maxDistance);

	bool mapPosition(dtPolyRef* nearestRef, float* nearestPt, const float* position, const float* extents, const dtQueryFilter& filter);

	bool raycast(NavMeshHit* hit, const float* sourcePosition, const float* targetPosition, const dtQueryFilter& filter);

	bool findPath(NavMeshPath* path, const float* sourcePosition, const float* targetPosition, const dtQueryFilter& filter);

private:

	struct LinearAllocator* m_talloc = nullptr;
	struct FastLZCompressor* m_tcomp = nullptr;
	struct MeshProcess* m_tmproc = nullptr;
	BuildContext* m_ctx = nullptr;

	class dtTileCache* m_tileCache = nullptr;

	class dtNavMesh* m_navMesh = nullptr;
	class dtNavMeshQuery* m_navQuery = nullptr;

	void clear();

	int rasterizeTileLayers(SceneRecastData& data, const int x, const int y, const rcConfig& cfg, TileCacheData* tiles, const int maxTiles);
};
