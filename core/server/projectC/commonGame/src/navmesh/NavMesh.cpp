#include "NavMesh.h"
#include <DetourAssert.h>
#include "utils/FileUtils.h"
#include "utils/MathUtils.h"
#include <DetourNavMeshQuery.h>
#include <assert.h>
#include <string.h>

void MeshProcess::process(struct dtNavMeshCreateParams* params, unsigned char* polyAreas, unsigned short* polyFlags)
{
	// Update poly flags from areas.
	for (int i = 0; i < params->polyCount; ++i)
	{
		unsigned int area = polyAreas[i] - RC_WALKABLE_AREA;
		assert(area < 32);

		// store area in flags as walkable mask
		polyFlags[i] = 1 << area;
		polyAreas[i] = area;


		/*if (polyAreas[i] == DT_TILECACHE_WALKABLE_AREA)
			polyAreas[i] = SAMPLE_POLYAREA_GROUND;

		if (polyAreas[i] == SAMPLE_POLYAREA_GROUND ||
			polyAreas[i] == SAMPLE_POLYAREA_GRASS ||
			polyAreas[i] == SAMPLE_POLYAREA_ROAD)
		{
			polyFlags[i] = SAMPLE_POLYFLAGS_WALK;
		}
		else if (polyAreas[i] == SAMPLE_POLYAREA_WATER)
		{
			polyFlags[i] = SAMPLE_POLYFLAGS_SWIM;
		}
		else if (polyAreas[i] == SAMPLE_POLYAREA_DOOR)d
		{
			polyFlags[i] = SAMPLE_POLYFLAGS_WALK | SAMPLE_POLYFLAGS_DOOR;
		}*/
	}

	//// Pass in off-mesh connections.
	//if (m_geom)
	//{
	//	params->offMeshConVerts = m_geom->getOffMeshConnectionVerts();
	//	params->offMeshConRad = m_geom->getOffMeshConnectionRads();
	//	params->offMeshConDir = m_geom->getOffMeshConnectionDirs();
	//	params->offMeshConAreas = m_geom->getOffMeshConnectionAreas();
	//	params->offMeshConFlags = m_geom->getOffMeshConnectionFlags();
	//	params->offMeshConUserID = m_geom->getOffMeshConnectionId();
	//	params->offMeshConCount = m_geom->getOffMeshConnectionCount();
	//}
}

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
		rcFreeHeightField(solid);
		delete[] triareas;
		rcFreeHeightfieldLayerSet(lset);
		rcFreeCompactHeightfield(chf);
		for (int i = 0; i < MAX_LAYERS; ++i)
		{
			dtFree(tiles[i].data);
			tiles[i].data = 0;
		}
	}

	rcHeightfield* solid;
	unsigned char* triareas;
	rcHeightfieldLayerSet* lset;
	rcCompactHeightfield* chf;
	TileCacheData tiles[MAX_LAYERS];
	int ntiles;
};



struct TileCacheTileHeader
{
	dtCompressedTileRef tileRef;
	int dataSize;
};

NavMesh::~NavMesh()
{
	clear();
}

void readVec3(BytesReadStream* stream, float* arr)
{
	arr[0] = stream->readFloat();
	arr[1] = stream->readFloat();
	arr[2] = stream->readFloat();
}

void vectorToFloatArr(const Vector3& vec, float* arr)
{
	arr[0] = vec.x;
	arr[1] = vec.y;
	arr[2] = vec.z;
}

void calcTriNormal(const float* v0, const float* v1, const float* v2, float* norm)
{
	float e0[3], e1[3];
	rcVsub(e0, v1, v0);
	rcVsub(e1, v2, v0);
	rcVcross(norm, e0, e1);
	rcVnormalize(norm);
}

//2->3
inline bool checkOverlapRect(const float amin[2], const float amax[2],
	const float bmin[3], const float bmax[3])
{
	bool overlap = true;
	overlap = (amin[0] > bmax[0] || amax[0] < bmin[0]) ? false : overlap;
	overlap = (amin[1] > bmax[2] || amax[1] < bmin[2]) ? false : overlap;
	return overlap;
}

int countArea(int srcArea, int norm, int walkableThr)
{
	int area;
	if (srcArea == kNotWalkable)
	{
		area = RC_FORCE_UNWALKABLE_AREA;
	}
	else
	{
		area = 0;
		if (norm > walkableThr)
		{
			area = RC_WALKABLE_AREA;
			area += srcArea;
			dtAssert(area < RC_FORCE_UNWALKABLE_AREA);
		}
	}

	return area;
}

rcConfig getRcConfig(const NavMeshConfig& settings, const SceneRecastData& data)
{
	//rcConfig config;
	//memset(&config, 0, sizeof(config));
	//config.cs = settings.cellSize;
	//config.ch = settings.cellHeight;
	//config.walkableSlopeAngle = settings.agentMaxSlope;
	//config.walkableHeight = (int)ceilf(settings.agentHeight / config.ch);
	//config.walkableClimb = (int)floorf(settings.agentMaxClimb / config.ch);
	//config.walkableRadius = (int)ceilf(settings.agentRadius / config.cs);
	//config.maxEdgeLen = (int)(settings.edgeMaxLen / settings.cellSize);
	//config.maxSimplificationError = settings.edgeMaxError;
	//config.minRegionArea = (int)rcSqr(settings.regionMinSize);		// Note: area = size*size
	//config.mergeRegionArea = (int)rcSqr(settings.regionMergeSize);	// Note: area = size*size
	//config.maxVertsPerPoly = (int)settings.vertsPerPoly;
	//config.tileSize = (int)settings.tileSize;
	//config.borderSize = config.walkableRadius + 3; // Reserve enough padding.
	//config.width = config.tileSize + config.borderSize * 2;
	//config.height = config.tileSize + config.borderSize * 2;
	//config.detailSampleDist = settings.detailSampleDist < 0.9f ? 0 : settings.cellSize * settings.detailSampleDist;
	//config.detailSampleMaxError = settings.cellHeight * settings.detailSampleMaxError;

	//float tempFArr[3];

	//vectorToFloatArr(data.min, tempFArr);
	//rcVcopy(config.bmin, tempFArr);
	//vectorToFloatArr(data.max, tempFArr);
	//rcVcopy(config.bmax, tempFArr);

	//return config;


	rcConfig config;
	memset(&config, 0, sizeof(config));
	float tempFArr[3];

	vectorToFloatArr(data.min, tempFArr);
	rcVcopy(config.bmin, tempFArr);
	vectorToFloatArr(data.max, tempFArr);
	rcVcopy(config.bmax, tempFArr);

	config.bmax[1] += settings.agentHeight + 0.1f; // Make sure the agent can stand at the top.

	config.cs = MathUtils::maxf(0.01f, settings.cellSize);

	// Height needs more precision so that walkableClimb is not too prone to rasterization errors.
	config.ch = config.cs * 0.5f;

	config.ch = 0.1f;

	// Some places inside Recast store height as a byte, make sure the ratio between
	// the agent height (plus climb) and cell height does not exceed this limit.
	if ((int)ceilf((settings.agentHeight + settings.agentMaxClimb) / config.ch) > 255)
	{
		const float cellHeight = (settings.agentHeight + settings.agentMaxClimb) / 255.0f;
		config.ch = cellHeight;
	}

	// Warn about too large vertical dimension and adjust the cell height to fit the whole world.
	float worldHeight = config.bmax[1] - config.bmin[1];
	int worldHeightVoxels = (int)ceilf(worldHeight / config.ch);
	if (worldHeightVoxels >= RC_SPAN_MAX_HEIGHT)
	{
		config.ch = worldHeight / (float)(RC_SPAN_MAX_HEIGHT);
	}

	config.walkableSlopeAngle = MathUtils::clampf(settings.agentMaxSlope, 0.1f, 90.0f);
	config.walkableHeight = (int)floorf(settings.agentHeight / config.ch);
	config.walkableClimb = (int)ceilf(settings.agentMaxClimb / config.ch);
	config.walkableRadius = MathUtils::maxf(0, (int)ceilf(settings.agentRadius / config.cs - config.cs * 0.01f));
	config.maxEdgeLen = 0;//(int)(settings.edgeMaxLen / settings.cellSize);
	config.maxSimplificationError = 1.3f;//settings.edgeMaxError;

	if (config.walkableClimb >= config.walkableHeight)
		config.walkableClimb = MathUtils::maxf(0, config.walkableHeight - 1);

	config.minRegionArea = (int)(settings.regionMinSize / (config.cs * config.cs)); //(int)rcSqr(settings.regionMinSize);		// Note: area = size*size
	config.tileSize = (int)settings.tileSize;
	config.mergeRegionArea = 400;// (int)rcSqr(settings.regionMergeSize);	// Note: area = size*size
	config.maxVertsPerPoly = kNavMeshVertsPerPoly;//(int)settings.vertsPerPoly;

	config.detailSampleDist = config.cs * 6.0f;
	config.detailSampleMaxError = config.ch * 1.0f;


	//config.borderSize = config.walkableRadius + 3; // Reserve enough padding.
	/*config.width = config.tileSize + config.borderSize * 2;
	config.height = config.tileSize + config.borderSize * 2;
	config.detailSampleDist = settings.detailSampleDist < 0.9f ? 0 : settings.cellSize * settings.detailSampleDist;
	config.detailSampleMaxError = settings.cellHeight * settings.detailSampleMaxError;*/

	rcCalcGridSize(config.bmin, config.bmax, config.cs, &config.width, &config.height);

	// Adjust for tile border
	config.borderSize = config.walkableRadius + 3;
	config.width = config.tileSize + 2 * config.borderSize;
	config.height = config.tileSize + 2 * config.borderSize;

	return config;
}

void rcFilterForceUnwalkableArea(rcContext* ctx, rcHeightfield& solid)
{
	const int w = solid.width;
	const int h = solid.height;

	for (int y = 0; y < h; ++y)
	{
		for (int x = 0; x < w; ++x)
		{
			for (rcSpan* s = solid.spans[x + y * w]; s; s = s->next)
			{
				if (s->area == RC_FORCE_UNWALKABLE_AREA)
				{
					s->area = RC_NULL_AREA;
				}
			}
		}
	}
}

void NavMesh::exportNav(const char* configPath, const char* dataPath, const char* savePath)
{
	BytesReadStream* configStream = FileUtils::readFileForBytesReadStream(configPath);
	BytesReadStream* dataStream = FileUtils::readFileForBytesReadStream(dataPath);

	if (!configStream || !dataStream)
	{
		print("config/data加载失败");
		return;
	}

	exportNav(configStream, dataStream, savePath);
}

void NavMesh::exportNav(BytesReadStream* configStream, BytesReadStream* dataStream, string savePath)
{
	NavMeshConfig config;
	config.read(configStream);

	SceneRecastData data;
	data.read(dataStream);

	clear();

	m_talloc = new LinearAllocator(512 * 1024);
	m_tcomp = new FastLZCompressor();
	m_tmproc = new MeshProcess();
	m_ctx = new BuildContext();

	m_tileCache = dtAllocTileCache();

	if (!m_tileCache)
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not allocate tile cache.");
		clear();
		return;
	}

	float tempFArr[3];

	dtTileCacheParams tcparams;
	memset(&tcparams, 0, sizeof(tcparams));

	vectorToFloatArr(data.min, tempFArr);
	rcVcopy(tcparams.orig, tempFArr);

	rcConfig rcConfig = getRcConfig(config, data);

	tcparams.cs = rcConfig.cs;
	tcparams.ch = rcConfig.ch;
	tcparams.width = (int)config.tileSize;
	tcparams.height = (int)config.tileSize;
	tcparams.walkableHeight = config.agentHeight;
	tcparams.walkableRadius = config.agentRadius;
	tcparams.walkableClimb = config.agentMaxClimb;
	tcparams.maxSimplificationError = config.edgeMaxError;
	tcparams.maxTiles = data.sizeX * data.sizeZ * EXPECTED_LAYERS_PER_TILE;
	tcparams.maxObstacles = 128;


	dtStatus status = m_tileCache->init(&tcparams, m_talloc, m_tcomp, m_tmproc);
	if (dtStatusFailed(status))
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not init tile cache.");
		clear();
		return;
	}

	m_navMesh = dtAllocNavMesh();
	if (!m_navMesh)
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not allocate navmesh.");
		clear();
		return;
	}

	/*int gw = (int)((data.max.x - data.min.x) / config.cellSize + 0.5f);
	int gh = (int)((data.max.z - data.min.z) / config.cellSize + 0.5f);
	const int ts = (int)config.tileSize;
	const int tw = (gw + ts - 1) / ts;
	const int th = (gh + ts - 1) / ts;*/

	// Max tiles and max polys affect how the tile IDs are caculated.
	// There are 22 bits available for identifying a tile and a polygon.
	int tileBits = rcMin((int)dtIlog2(dtNextPow2(data.sizeX * data.sizeZ * EXPECTED_LAYERS_PER_TILE)), 14);
	if (tileBits > 14) tileBits = 14;
	int polyBits = 22 - tileBits;
	int m_maxTiles = 1 << tileBits;
	int m_maxPolysPerTile = 1 << polyBits;

	dtNavMeshParams params;
	memset(&params, 0, sizeof(params));
	rcVcopy(params.orig, tcparams.orig);
	params.tileWidth = config.tileSize * config.cellSize;
	params.tileHeight = config.tileSize * config.cellSize;
	params.maxTiles = m_maxTiles;
	params.maxPolys = m_maxPolysPerTile;

	status = m_navMesh->init(&params);
	if (dtStatusFailed(status))
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not init navmesh.");
		clear();
		return;
	}

	// Build initial meshes
	int64 start = Ctrl::getTimer();

	SList<TileCacheData> list;

	for (int y = 0; y < data.sizeZ; ++y)
	{
		for (int x = 0; x < data.sizeX; ++x)
		{
			TileCacheData tiles[MAX_LAYERS];
			int nTiles = rasterizeTileLayers(data, x, y, rcConfig, tiles, MAX_LAYERS);

			for (int i = 0; i < nTiles; ++i)
			{
				TileCacheData* tile = &tiles[i];
				status = m_tileCache->addTile(tile->data, tile->dataSize, DT_COMPRESSEDTILE_FREE_DATA, 0);
				if (dtStatusFailed(status))
				{
					dtFree(tile->data);
					tile->data = 0;

					m_ctx->log(RC_LOG_ERROR, "addTileFailed.");
					clear();
					return;
				}

				//list.add(move(*tile));
				list.push_back(move(*tile));

				status = m_tileCache->buildNavMeshTilesAt(x, y, m_navMesh);

				if (dtStatusFailed(status))
				{
					m_ctx->log(RC_LOG_ERROR, "buildTileFailed.");
					clear();
					return;
				}
			}
		}
	}

	int64 end = Ctrl::getTimer();

	print("cost time %d", end - start);

	save(savePath.c_str());

	clear();

	print("OK");
}


void NavMesh::save(string path)
{
	if (!m_tileCache)
		return;

	FILE* fp = fopen(path.c_str(), "wb");

	if (!fp)
	{
		Ctrl::errorLog("无法打开目录 %s", path.c_str());
		return;
	}

	// Store header.
	TileCacheSetHeader header;
	header.magic = TILECACHESET_MAGIC;
	header.version = TILECACHESET_VERSION;
	header.numTiles = 0;
	for (int i = 0; i < m_tileCache->getTileCount(); ++i)
	{
		const dtCompressedTile* tile = m_tileCache->getTile(i);
		if (!tile || !tile->header || !tile->dataSize) continue;
		header.numTiles++;
	}
	memcpy(&header.cacheParams, m_tileCache->getParams(), sizeof(dtTileCacheParams));
	memcpy(&header.meshParams, m_navMesh->getParams(), sizeof(dtNavMeshParams));
	fwrite(&header, sizeof(TileCacheSetHeader), 1, fp);

	// Store tiles.
	for (int i = 0; i < m_tileCache->getTileCount(); ++i)
	{
		const dtCompressedTile* tile = m_tileCache->getTile(i);
		if (!tile || !tile->header || !tile->dataSize) continue;

		TileCacheTileHeader tileHeader;
		tileHeader.tileRef = m_tileCache->getTileRef(tile);
		tileHeader.dataSize = tile->dataSize;
		fwrite(&tileHeader, sizeof(tileHeader), 1, fp);

		fwrite(tile->data, tile->dataSize, 1, fp);
	}

	fclose(fp);
}


void NavMesh::clear()
{
	if (m_talloc)
	{
		delete m_talloc;
		m_talloc = nullptr;
	}

	if (m_tcomp)
	{
		delete m_tcomp;
		m_tcomp = nullptr;
	}

	if (m_tmproc)
	{
		delete m_tmproc;
		m_tmproc = nullptr;
	}

	if (m_tileCache)
	{
		delete m_tileCache;
		m_tileCache = nullptr;
	}

	if (m_ctx)
	{
		delete m_ctx;
		m_ctx = nullptr;
	}

	/*if (m_navMesh)
	{
		delete m_navMesh;
		m_navMesh = nullptr;
	}

	if (m_navQuery)
	{
		delete m_navQuery;
		m_navQuery = nullptr;
	}*/

}

int NavMesh::rasterizeTileLayers(SceneRecastData& data, const int x, const int y, const rcConfig& cfg, TileCacheData* tiles, const int maxTiles)
{
	FastLZCompressor comp;
	RasterizationContext rc;

	// Tile bounds.
	const float tcs = cfg.tileSize * cfg.cs;

	rcConfig tcfg;
	memcpy(&tcfg, &cfg, sizeof(tcfg));

	int tx = x;
	int ty = y;

	tcfg.bmin[0] = cfg.bmin[0] + tx * tcs;
	tcfg.bmin[1] = cfg.bmin[1];
	tcfg.bmin[2] = cfg.bmin[2] + ty * tcs;
	tcfg.bmax[0] = cfg.bmin[0] + (tx + 1) * tcs;
	tcfg.bmax[1] = cfg.bmax[1];
	tcfg.bmax[2] = cfg.bmin[2] + (ty + 1) * tcs;
	tcfg.bmin[0] -= tcfg.borderSize * tcfg.cs;
	tcfg.bmin[2] -= tcfg.borderSize * tcfg.cs;
	tcfg.bmax[0] += tcfg.borderSize * tcfg.cs;
	tcfg.bmax[2] += tcfg.borderSize * tcfg.cs;

	// Allocate voxel heightfield where we rasterize our input data to.
	rc.solid = rcAllocHeightfield();
	if (!rc.solid)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Out of memory 'solid'.");
		return 0;
	}

	if (!rcCreateHeightfield(nullptr, *rc.solid, tcfg.width, tcfg.height, tcfg.bmin, tcfg.bmax, tcfg.cs, tcfg.ch))
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not create solid heightfield.");
		return 0;
	}

	const float walkableThr = cosf(tcfg.walkableSlopeAngle / 180.0f * RC_PI);
	int area = 0;
	float norm[3];

	float v0[3];
	float v1[3];
	float v2[3];
	float v3[3];

	float va0[3];
	float va1[3];
	float va2[3];

	SceneRecastTerrainData* terrain = data.terrain;

	//first terrain
	if (terrain)
	{
		int x0 = floor((tcfg.bmin[0] - terrain->origin.x) / terrain->unit.x);
		int z0 = floor((tcfg.bmin[2] - terrain->origin.z) / terrain->unit.z);
		int x1 = ceil((tcfg.bmax[0] - terrain->origin.x) / terrain->unit.x);
		int z1 = ceil((tcfg.bmax[2] - terrain->origin.z) / terrain->unit.z);

		if (z0 < 0)
			z0 = 0;
		if (x0 < 0)
			x0 = 0;
		if (x1 >= terrain->resolutionX)
			x1 = terrain->resolutionX - 1;
		if (z1 >= terrain->resolutionZ)
			z1 = terrain->resolutionZ - 1;

		for (int j = z0; j <= z1; ++j)
		{
			for (int i = x0; i <= x1; ++i)
			{
				terrain->getVertex(i, j, v0);
				terrain->getVertex(i, j + 1, v1);
				terrain->getVertex(i + 1, j, v2);
				terrain->getVertex(i + 1, j + 1, v3);

				calcTriNormal(v0, v1, v3, norm);
				area = 0;
				if (norm[1] > walkableThr)
					area = RC_WALKABLE_AREA;
				rcRasterizeTriangle(m_ctx, v0, v1, v3, area, *rc.solid);

				calcTriNormal(v3, v2, v0, norm);
				area = 0;
				if (norm[1] > walkableThr)
					area = RC_WALKABLE_AREA;
				rcRasterizeTriangle(m_ctx, v3, v2, v0, area, *rc.solid);
			}
		}
	}

	//then mesh
	float tbmin[2], tbmax[2];
	tbmin[0] = tcfg.bmin[0];
	tbmin[1] = tcfg.bmin[2];
	tbmax[0] = tcfg.bmax[0];
	tbmax[1] = tcfg.bmax[2];

	for (int i = 0; i < data.objListSize; i++)
	{
		SceneRecastObjData* obj = data.objList[i];

		if (checkOverlapRect(tbmin, tbmax, obj->min, obj->max))
		{
			SceneRecastMeshData* meshObj = data.meshList[obj->meshIndex];

			int* triangles = meshObj->triangles;
			Vector3* vertices = meshObj->vertices;

			int triCount = meshObj->trianglesSize / 3;
			int jLen = meshObj->trianglesSize;

			for (int j = 0; j < jLen; j += 3)
			{
				vectorToFloatArr(vertices[triangles[j]], va0);
				vectorToFloatArr(vertices[triangles[j + 1]], va1);
				vectorToFloatArr(vertices[triangles[j + 2]], va2);

				obj->transVert(va0, v0);
				obj->transVert(va1, v1);
				obj->transVert(va2, v2);

				calcTriNormal(v0, v1, v2, norm);

				area = countArea(obj->area, norm[1], walkableThr);

				rcRasterizeTriangle(m_ctx, v0, v1, v2, area, *rc.solid);
			}
		}
	}

	rcFilterForceUnwalkableArea(m_ctx, *rc.solid);
	rcFilterLowHangingWalkableObstacles(m_ctx, tcfg.walkableClimb, *rc.solid);
	rcFilterLedgeSpans(m_ctx, tcfg.walkableHeight, tcfg.walkableClimb, *rc.solid);
	rcFilterWalkableLowHeightSpans(m_ctx, tcfg.walkableHeight, *rc.solid);

	// Skip if tile is empty, i.e. it has no spans allocated
	/*if (*rc.solid->freelist == NULL)
		return 0;*/

	rc.chf = rcAllocCompactHeightfield();
	if (!rc.chf)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Out of memory 'chf'.");
		return 0;
	}
	if (!rcBuildCompactHeightfield(m_ctx, tcfg.walkableHeight, tcfg.walkableClimb, *rc.solid, *rc.chf))
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not build compact data.");
		return 0;
	}

	// Erode the walkable area by agent radius.
	if (!rcErodeWalkableArea(m_ctx, tcfg.walkableRadius, *rc.chf))
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not erode.");
		return 0;
	}


	rc.lset = rcAllocHeightfieldLayerSet();
	if (!rc.lset)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Out of memory 'lset'.");
		return 0;
	}
	if (!rcBuildHeightfieldLayers(m_ctx, *rc.chf, tcfg.borderSize, tcfg.walkableHeight, *rc.lset))
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not build heighfield layers.");
		return 0;
	}

	rc.ntiles = 0;

	if (rc.lset->nlayers >= maxTiles)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: layers num above maxTiles.");
	}

	int nt = rcMin(rc.lset->nlayers, maxTiles);

	for (int i = 0; i < nt; ++i)
	{
		TileCacheData* tile = &rc.tiles[rc.ntiles++];
		const rcHeightfieldLayer* layer = &rc.lset->layers[i];

		// Store header
		dtTileCacheLayerHeader header;
		header.magic = DT_TILECACHE_MAGIC;
		header.version = DT_TILECACHE_VERSION;

		// Tile layer location in the navmesh.
		header.tx = tx;
		header.ty = ty;
		header.tlayer = i;
		dtVcopy(header.bmin, layer->bmin);
		dtVcopy(header.bmax, layer->bmax);

		// Tile info.
		header.width = (unsigned short)layer->width;
		header.height = (unsigned short)layer->height;
		header.minx = (unsigned short)layer->minx;
		header.maxx = (unsigned short)layer->maxx;
		header.miny = (unsigned short)layer->miny;
		header.maxy = (unsigned short)layer->maxy;
		header.hmin = (unsigned short)layer->hmin;
		header.hmax = (unsigned short)layer->hmax;

		dtStatus status = dtBuildTileCacheLayer(&comp, &header, layer->heights, layer->areas, layer->cons,
			&tile->data, &tile->dataSize);
		if (dtStatusFailed(status))
		{
			return 0;
		}
	}

	// Transfer ownsership of tile data from build context to the caller.
	int n = 0;
	for (int i = 0; i < nt; ++i)
	{
		tiles[n++] = rc.tiles[i];
		rc.tiles[i].data = 0;
		rc.tiles[i].dataSize = 0;
	}

	return nt;
}

void NavMesh::readBytes(char* bytes, int len)
{
	BytesReadStream stream(bytes, len);

	TileCacheSetHeader header;
	stream.readMem(&header, sizeof(TileCacheSetHeader));

	if (header.magic != TILECACHESET_MAGIC)
	{
		Ctrl::errorLog("read magic 出错");
		clear();
		return;
	}
	if (header.version != TILECACHESET_VERSION)
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	m_navMesh = dtAllocNavMesh();
	if (!m_navMesh)
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	dtStatus status = m_navMesh->init(&header.meshParams);
	if (dtStatusFailed(status))
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	m_tileCache = dtAllocTileCache();
	if (!m_tileCache)
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	m_talloc = new LinearAllocator(512 * 1024);
	m_tcomp = new FastLZCompressor();
	m_tmproc = new MeshProcess();

	status = m_tileCache->init(&header.cacheParams, m_talloc, m_tcomp, m_tmproc);
	if (dtStatusFailed(status))
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	// Read tiles.
	for (int i = 0; i < header.numTiles; ++i)
	{
		TileCacheTileHeader tileHeader;
		stream.readMem(&tileHeader, sizeof(TileCacheTileHeader));

		if (!tileHeader.tileRef || !tileHeader.dataSize)
			break;

		unsigned char* data = (unsigned char*)dtAlloc(tileHeader.dataSize, DT_ALLOC_PERM);
		if (!data)
			break;
		//memset(data, 0, tileHeader.dataSize);
		stream.readMem(data, tileHeader.dataSize);

		dtCompressedTileRef tile = 0;
		dtStatus addTileStatus = m_tileCache->addTile(data, tileHeader.dataSize, DT_COMPRESSEDTILE_FREE_DATA, &tile);

		if (dtStatusFailed(addTileStatus))
		{
			dtFree(data);
		}

		if (tile)
		{
			dtStatus buildStatus = m_tileCache->buildNavMeshTile(tile, m_navMesh);

			if (dtStatusFailed(buildStatus))
			{
				dtFree(data);
			}
		}
	}

	m_navQuery = dtAllocNavMeshQuery();

	//m_crowd = dtAllocCrowd();
	m_navQuery->init(m_navMesh, 4096);
}

float magnitude(float x, float y, float z)
{
	return rcSqrt(rcSqr(x) + rcSqr(y) + rcSqr(z));
}

float magnitude(const float* start, const float* end)
{
	return rcSqrt(rcSqr(end[0] - start[0]) + rcSqr(end[1] - start[1]) + rcSqr(end[2] - start[2]));
}

void lerp(float* re, const float* start, const float* end, float t)
{
	re[0] = start[0] + (end[0] - start[0]) * t;
	re[1] = start[1] + (end[1] - start[1]) * t;
	re[2] = start[2] + (end[2] - start[2]) * t;
}

bool NavMesh::samplePosition(NavMeshHit* hit, const float* position, const dtQueryFilter& filter, float maxDistance)
{
	hit->clear();

	dtPolyRef ref;
	float extents[3];
	extents[0] = maxDistance;
	extents[1] = maxDistance;
	extents[2] = maxDistance;

	float nearestPt[3];

	if (!mapPosition(&ref, nearestPt, position, extents, filter))
	{
		return false;
	}

	const float distance = magnitude(nearestPt, position);
	if (distance > maxDistance)
	{
		return false;
	}

	uint16 flag;
	m_navMesh->getPolyFlags(ref, &flag);

	hit->setPosition(nearestPt);
	hit->distance = distance;
	hit->mask = flag;
	hit->hit = true;

	return true;
}

bool NavMesh::mapPosition(dtPolyRef* nearestRef, float* nearestPt, const float* position, const float* extents, const dtQueryFilter& filter)
{
	if (!m_navQuery)
		return false;

	m_navQuery->findNearestPoly(position, extents, &filter, nearestRef, nearestPt);

	dtPolyRef ref = *nearestRef;

	return ref != 0;
}

bool NavMesh::raycast(NavMeshHit* hit, const float* sourcePosition, const float* targetPosition, const dtQueryFilter& filter)
{
	hit->clear();

	dtPolyRef ref;
	float mappedPosition[3];

	const dtTileCacheParams* params = m_tileCache->getParams();

	float extends[3];
	extends[0] = params->walkableRadius;
	extends[1] = params->walkableHeight;
	extends[2] = params->walkableRadius;

	if (!mapPosition(&ref, mappedPosition, sourcePosition, extends, filter))
	{
		return false;
	}

	//	float* t, float* hitNormal, dtPolyRef* path, int* pathCount, const int maxPath

	//	NavMeshRaycastResult result;
	float t = 0;
	float normal[3];
	dtStatus status = m_navQuery->raycast(ref, mappedPosition, targetPosition,
		&filter, &t, normal, NULL, NULL, 0);

	if (dtStatusFailed(status))
	{
		return false;
	}

	if (t > 1.0f)
		t = 1.0f;

	float rePos[3];
	lerp(rePos, mappedPosition, targetPosition, t);
	bool blocked = t < 1.0f;

	//	Vector3f pos;
	//	m_NavMeshQuery->ProjectToPoly(&pos, result.lastPoly, lpos);
	//	m_HeightMeshQuery->SetPositionHeight(&pos);

	hit->setPosition(rePos);
	hit->setNormal(normal);

	hit->distance = magnitude(hit->position, sourcePosition);

	//	hit->mask = m_navMesh->getPolyFlags()(result.hitPoly);
		// hit->mask=0;
	hit->hit = blocked;

	return blocked;
}

bool NavMesh::findPath(NavMeshPath* path, const float* sourcePosition, const float* targetPosition, const dtQueryFilter& filter)
{
	dtPolyRef startRef;
	dtPolyRef endRef;

	const dtTileCacheParams* params = m_tileCache->getParams();

	float extends[3];
	extends[0] = params->walkableRadius;
	extends[1] = params->walkableHeight;
	extends[2] = params->walkableRadius;

	float startPos[3];

	if (!mapPosition(&startRef, startPos, sourcePosition, extends, filter))
	{
		return false;
	}

	float endPos[3];

	if (!mapPosition(&endRef, endPos, targetPosition, extends, filter))
	{
		return false;
	}

	dtPolyRef polyRefs[MaxPathNum];

	int nPolys;

	dtStatus re = m_navQuery->findPath(startRef, endRef, startPos, endPos, &filter, polyRefs, &nPolys, MaxPathNum);

	if (dtStatusFailed(re))
	{
		return false;
	}

	if (nPolys > 0)
	{
		// In case of partial path, make sure the end point is clamped to the last polygon.
		float epos[3];
		dtVcopy(epos, endPos);
		if (polyRefs[nPolys - 1] != endRef)
			m_navQuery->closestPointOnPoly(polyRefs[nPolys - 1], endPos, epos, 0);

		dtPolyRef straightPathRefs[MaxPathNum];

		dtStatus qre = m_navQuery->findStraightPath(startPos, endPos, polyRefs, nPolys, path->path, NULL, straightPathRefs, &path->pointNum, MaxPathNum);

		if (dtStatusFailed(qre))
		{
			return false;
		}

		return true;
	}

	return false;
}

void NavMeshConfig::read(BytesReadStream* stream)
{
	agentHeight = stream->readFloat();
	agentRadius = stream->readFloat();
	agentMaxClimb = stream->readFloat();
	agentMaxSlope = stream->readFloat();

	tileSize = stream->readFloat();
	cellSize = stream->readFloat();

	regionMinSize = stream->readFloat();
	edgeMaxError = stream->readFloat();
}


SceneRecastTerrainData::~SceneRecastTerrainData()
{
	if (heightSamples)
	{
		delete[] heightSamples;
		heightSamples = nullptr;
	}
}

void SceneRecastTerrainData::read(BytesReadStream* stream)
{
	origin = stream->readVector3();
	unit = stream->readVector3();
	resolutionX = stream->readInt();
	resolutionZ = stream->readInt();

	int len = stream->readLen();

	heightSamples = new uint16[len];

	for (int i = 0; i < len; ++i)
	{
		heightSamples[i] = static_cast<uint16>(stream->natureReadShort());
	}
}


void SceneRecastTerrainData::getVertex(int x, int z, float* out)
{
	if (z < 0)
		z = 0;
	if (x < 0)
		x = 0;
	if (x >= resolutionX)
		x = resolutionX - 1;
	if (z >= resolutionZ)
		z = resolutionZ - 1;

	out[0] = origin.x + (x * unit.x);
	out[2] = origin.z + (z * unit.z);

	uint16 y = heightSamples[x + z * resolutionX];
	out[1] = origin.y + y * unit.y;
	out[1] = 0;
}

SceneRecastMeshData::~SceneRecastMeshData()
{
	if (triangles)
	{
		delete[] triangles;
		triangles = nullptr;
	}

	if (vertices)
	{
		delete[] vertices;
		vertices = nullptr;
	}
}

void SceneRecastMeshData::read(BytesReadStream* stream)
{
	int len = trianglesSize = stream->readLen();

	triangles = new int[len];

	for (int i = 0; i < len; ++i)
	{
		triangles[i] = stream->readInt();
	}

	len = verticesSize = stream->readLen();
	vertices = new Vector3[len];

	for (int i = 0; i < len; ++i)
	{
		vertices[i] = stream->readVector3();
	}

	min = stream->readVector3();
	max = stream->readVector3();
}

void SceneRecastObjData::read(BytesReadStream* stream)
{
	meshIndex = stream->readInt();
	area = stream->readInt();
	readVec3(stream, x);
	readVec3(stream, y);
	readVec3(stream, z);
	readVec3(stream, w);
	readVec3(stream, min);
	readVec3(stream, max);
}


void SceneRecastObjData::transVert(float* src, float* rtn)
{
	rcVmad(rtn, w, x, src[0]);
	rcVmad(rtn, rtn, y, src[1]);
	rcVmad(rtn, rtn, z, src[2]);
}

SceneRecastData::~SceneRecastData()
{
	if (terrain)
	{
		delete terrain;
		terrain = nullptr;
	}

	if (meshList)
	{
		int len = meshListSize;
		for (int i = 0; i < len; ++i)
		{
			delete meshList[i];
		}

		delete[] meshList;
		meshList = nullptr;
	}
}

void SceneRecastData::read(BytesReadStream* stream)
{
	originX = stream->readInt();
	originZ = stream->readInt();
	sizeX = stream->readInt();
	sizeZ = stream->readInt();
	tileSize = stream->readFloat();

	min = stream->readVector3();
	max = stream->readVector3();

	bool hasTerrain = stream->readBoolean();

	if (hasTerrain)
	{
		terrain = new SceneRecastTerrainData();
		terrain->read(stream);
	}
	else
	{
		terrain = nullptr;
	}

	int len = meshListSize = stream->readLen();
	meshList = new SceneRecastMeshData * [len];

	for (int i = 0; i < len; ++i)
	{
		SceneRecastMeshData* mData = new SceneRecastMeshData();
		mData->read(stream);
		meshList[i] = mData;
	}

	len = objListSize = stream->readLen();
	objList = new SceneRecastObjData * [len];

	for (int i = 0; i < len; ++i)
	{
		SceneRecastObjData* oData = new SceneRecastObjData();
		oData->read(stream);
		objList[i] = oData;
	}
}

void BuildContext::doLog(const rcLogCategory category, const char* msg, const int len)
{
	Ctrl::print(string(msg, len));
}
