#pragma once

#include "SInclude.h"
#include "bytes/BytesReadStream.h"
#include "bytes/BytesWriteStream.h"
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
#include "dataEx/ExternBuf.h"
#include <math.h>

static const int EXPECTED_LAYERS_PER_TILE = 4;
static const int MAX_LAYERS = 32;

static const int TILECACHESET_MAGIC = 'T' << 24 | 'S' << 16 | 'E' << 8 | 'T'; //'TSET';
static const int TILECACHESET_VERSION = 1;

static const int kNavMeshVertsPerPoly = 6;
static const int kMaxNeis = 4;

static const int kMaxSegsPerPoly = kNavMeshVertsPerPoly * 3;

static const int kWalkable = 0;
static const int kNotWalkable = 1;
static const int kJump = 2;


static const int MaxPathNum = 256;

// Keep this enum in sync with the one defined in "OffMeshLink.bindings.cs"
enum OffMeshLinkType
{
	kLinkTypeManual = 0,
	kLinkTypeDropDown = 1,
	kLinkTypeJumpAcross = 2
};

enum NavMeshLinkDirectionFlags
{
	kLinkDirectionOneWay = 0,
	kLinkDirectionTwoWay = 1
};

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

struct AutoLinkPoints
{
	Vector3 start;
	Vector3 end;
};

struct EdgeSegment
{
	Vector3 start;
	Vector3 end;
	Vector3 normal;
};

// OffMeshLinkData is a scripting API type.
struct AutoOffMeshLinkData
{
	Vector3 m_Start;
	Vector3 m_End;
	float m_Radius;
	unsigned short m_LinkType;      // Off-mesh poly flags.
	unsigned char m_Area;           // Off-mesh poly  area ids.
	unsigned char m_LinkDirection;  // Off-mesh connection direction flags (NavMeshLinkDirectionFlags)
};

struct RasterizationContext
{
	RasterizationContext() :
		solid(0),
		triareas(0),
		lset(0),
		chf(0),
		ntiles(0)
	{
		memset(tiles, 0, sizeof(TileCacheData) * MAX_LAYERS);
	}

	~RasterizationContext()
	{
		if (solid)
		{
			rcFreeHeightField(solid);
			solid = nullptr;
		}

		if (triareas)
		{
			delete[] triareas;
			triareas = nullptr;
		}

		if (lset)
		{
			rcFreeHeightfieldLayerSet(lset);
			lset = nullptr;
		}

		if (chf)
		{
			rcFreeCompactHeightfield(chf);
			chf = nullptr;
		}

		for (int i = 0; i < MAX_LAYERS; ++i)
		{
			if (tiles[i].data)
			{
				dtFree(tiles[i].data);
				tiles[i].data = 0;
			}
		}
	}

	rcHeightfield* solid;
	unsigned char* triareas;
	rcHeightfieldLayerSet* lset;
	rcCompactHeightfield* chf;
	TileCacheData tiles[MAX_LAYERS];
	int ntiles;
};

struct TileLayerInfo
{
	dtTileRef ref = 0;
	bool* autoOML = nullptr;

	TileLayerInfo()
	{

	}

	~TileLayerInfo()
	{
		clear();
	}

	void clear()
	{
		if (autoOML)
		{
			delete[] autoOML;
			autoOML = nullptr;
		}
	}
};

struct TileInfo
{
	int ntiles = 0;
	TileLayerInfo tileLayers[MAX_LAYERS];

	RasterizationContext* rc = nullptr;

	~TileInfo()
	{
		clear();
	}

	void clear()
	{
		if (ntiles)
		{
			for (int i = 0; i < ntiles; ++i)
			{
				tileLayers[i].clear();
			}

			ntiles = 0;
		}


		if (rc)
		{
			delete rc;
			rc = nullptr;
		}
	}
};

class NavMesh;

struct MeshProcess : public dtTileCacheMeshProcess
{
public:
	NavMesh* m_nm = nullptr;

	~MeshProcess()
	{
		if (m_nm)
		{
			m_nm = nullptr;
		}
	}

	virtual void process(struct dtNavMeshCreateParams* params, unsigned char* polyAreas, unsigned int* polyFlags)override;
};

class NavMeshConfig
{
public:
	float agentHeight = 2.0f;
	float agentRadius = 0.5f;
	float agentMaxClimb = 0.4f;
	float agentMaxSlope = 45.0f;

	float dropHeight = 10.0f;
	float jumpDistance = 2.0f;

	float tileSize = 64;
	float cellSize = 0.5f;

	float minRegionArea = 2;
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

	int area;
	bool autoOML;

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



class BuildContext : public rcContext
{
public:

	NavMesh* m_nm = nullptr;

	NavMeshConfig config;

	rcConfig rcConfig;

	TileInfo* m_recastTiles = nullptr;

	int sizeX;
	int sizeZ;
	float tileSize;

	BuildContext()
	{
		sizeX = 0;
		sizeZ = 0;
		tileSize = 0.0f;
	}

	~BuildContext()
	{
		if (m_nm)
		{
			m_nm = nullptr;
		}

		if (m_recastTiles)
		{
			delete[] m_recastTiles;
			m_recastTiles = nullptr;
		}
	}

	void setTile(int x, int z, const TileInfo& recastTile)
	{
		getTile(x, z) = recastTile;
	}

	TileInfo& getTile(int x, int z) const
	{
		int index = x + z * sizeX;
		return m_recastTiles[index];
	}

	TileLayerInfo& getTileLayer(int x, int z, int layer)
	{
		return getTile(x, z).tileLayers[layer];
	}

	bool inRange(int x, int z) const
	{
		return x >= 0 && z >= 0 && x < sizeX && z < sizeZ;
	}

	void clearTile(int x, int z)
	{
		getTile(x, z).clear();
	}

	bool calcTileLoc(float posX, float posZ, int* ix, int* iz) const
	{
		*ix = (int)floorf(posX / tileSize);
		*iz = (int)floorf(posZ / tileSize);
		return inRange(*ix, *iz);
	}

	bool checkHeightfieldCollision(const float x, const float ymin, const float ymax, const float z) const;

	int getNearestHeight(const float x, const float y, const float z, float* h) const;

protected:

	virtual void doLog(const rcLogCategory category, const char* msg, const int len);
};

//result

class NavMeshPath
{
public:
	float path[3 * MaxPathNum];
	unsigned char areas[MaxPathNum];
	int pointNum = 0;

	void writeBuf(ExternBuf* buf)
	{
		//不写起始点

		buf->writeInt(pointNum - 1);

		for (int i = 1; i < pointNum; ++i)
		{
			buf->writeVector(&path[i * 3]);
			buf->writeInt(areas[i]);
		}
	}
};

class NavMeshHit
{
public:
	float position[3];
	float normal[3];
	float distance = 0;
	uint16 mask = 0;
	bool hit = false;

	void setPosition(float* value)
	{
		position[0] = value[0];
		position[1] = value[1];
		position[2] = value[2];
	}

	void setNormal(float* value)
	{
		normal[0] = value[0];
		normal[1] = value[1];
		normal[2] = value[2];
	}

	void clear()
	{
		position[0] = 0;
		position[1] = 0;
		position[2] = 0;
		normal[0] = 0;
		normal[1] = 0;
		normal[2] = 0;
		distance = 0;
		mask = 0;
		hit = false;
	}

	void writeBuf(ExternBuf* buf)
	{
		buf->writeVector(position);
		buf->writeVector(normal);
		buf->writeFloat(distance);
		buf->writeInt(mask);
		buf->writeBoolean(hit);
	}
};

class NavMesh
{
public:
	BuildContext* m_ctx = nullptr;

	NavMesh()
	{

	}

	~NavMesh()
	{
		clear();
	}

	//是否读取过程
	bool isReading = false;

	//offMesLink
	int offMeshConCount;
	float* offMeshConVerts = nullptr;
	float* offMeshConRad = nullptr;
	unsigned int* offMeshConFlags = nullptr;
	unsigned char* offMeshConAreas = nullptr;
	unsigned char* offMeshConDir = nullptr;
	unsigned int* offMeshConUserID = nullptr;

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

	void addOffMeshConnection(const Vector3& start, const Vector3& end, const float radius,
		bool bidirectional, unsigned char area, OffMeshLinkType linkType);

private:

	struct LinearAllocator* m_talloc = nullptr;
	struct FastLZCompressor* m_tcomp = nullptr;
	struct MeshProcess* m_tmproc = nullptr;


	class dtTileCache* m_tileCache = nullptr;
	class dtNavMesh* m_navMesh = nullptr;
	class dtNavMeshQuery* m_navQuery = nullptr;

	vector<AutoOffMeshLinkData> m_OffMeshLinks;

	void clear();

	void clearOML();

	/** 构造OML数据 */
	void makeOML();

	int rasterizeTileLayers(SceneRecastData& data, const int x, const int y, const rcConfig& cfg, TileCacheData* tiles, const int maxTiles, RasterizationContext& rc);

	void appendOffMeshLinks(const int x, const int z);

	void getMeshEdges(int x, int z, vector<EdgeSegment>& autoLinkEdgeSegments);

	void placeDropDownLinks(vector<EdgeSegment>& autoLinkEdgeSegments);

	void findValidDropDowns(vector<EdgeSegment>& autoLinkEdgeSegments, vector<AutoLinkPoints>& autoLinkPoints);

	void getSubsampledLocations(Vector3 segmentStart, Vector3 segmentEnd, float subsampleDistance, vector<Vector3>& locations);

	float verticalNavMeshTest(Vector3 testFrom, float testHeight);

	bool dropDownBlocked(Vector3 startPos, Vector3 endPos, float cs, float ch);

	void placeJumpAcrossLinks(vector<EdgeSegment>& autoLinkEdgeSegments);
	void findValidJumpAcrossLinks(vector<EdgeSegment>& autoLinkEdgeSegments, vector<AutoLinkPoints>& autoLinkPoints);
	bool jumpAcrossBlocked(Vector3 startPos, Vector3 endPos, float cs, float ch);

	//navmeshQuery
	dtStatus getPolyHeightLocal(dtPolyRef ref, const Vector3& pos, float* height);
};

// Checks for a heightfield collision
// Params:
// x, z - (in) x and z coordinates to test
// ymin, ymax - (in) y range to test
// hf - (in) rcHeightfield to test inside
bool rcCheckHeightfieldCollision(const float x, const float ymin, const float ymax, const float z, const rcHeightfield& hf);

// Get nearest heightfield height below specified heightfield location.
// Params:
// x, y, z - (in) x, y and z coordinates of the sampling point.
// hf - (in) rcHeightfield to query
bool rcGetNearestHeight(const float x, const float y, const float z, const rcHeightfield& hf, float* height);
