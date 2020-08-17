#include "NavMesh.h"
#include <DetourAssert.h>
#include "utils/FileUtils.h"
#include "utils/MathUtils.h"
#include <DetourNavMeshQuery.h>
#include <assert.h>
#include <string.h>
#include <float.h>

void MeshProcess::process(struct dtNavMeshCreateParams* params, unsigned char* polyAreas, unsigned int* polyFlags)
{
	if (!m_nm->isReading)
	{
		TileLayerInfo& tileLayer = m_nm->m_ctx->getTileLayer(params->tileX, params->tileY, params->tileLayer);

		dtAssert(tileLayer.autoOML == nullptr);

		tileLayer.autoOML = new bool[params->polyCount];

		// Update poly flags from areas.
		for (int i = 0; i < params->polyCount; ++i)
		{
			unsigned char area = polyAreas[i] - RC_WALKABLE_AREA;

			bool autoOML = area & 0x1;

			tileLayer.autoOML[i] = autoOML;

			area >>= 1;
			dtAssert(area < 32);

			// store area in flags as walkable mask
			polyFlags[i] = 1 << area;
			polyAreas[i] = area;
		}
	}
	else
	{
		// Update poly flags from areas.
		for (int i = 0; i < params->polyCount; ++i)
		{
			unsigned int area = polyAreas[i] - RC_WALKABLE_AREA;

			//bool autoOML = area & 0x1;
			//tileLayer.autoOML[i] = autoOML;

			area >>= 1;
			dtAssert(area < 32);

			// store area in flags as walkable mask
			polyFlags[i] = 1 << area;
			polyAreas[i] = area;
		}

		params->offMeshConCount = m_nm->offMeshConCount;
		params->offMeshConVerts = m_nm->offMeshConVerts;
		params->offMeshConRad = m_nm->offMeshConRad;
		params->offMeshConDir = m_nm->offMeshConDir;
		params->offMeshConAreas = m_nm->offMeshConAreas;
		params->offMeshConFlags = m_nm->offMeshConFlags;
		params->offMeshConUserID = m_nm->offMeshConUserID;
	}
}

struct TileCacheTileHeader
{
	dtCompressedTileRef tileRef;
	int dataSize;
};

bool BuildContext::checkHeightfieldCollision(const float x, const float ymin, const float ymax, const float z) const
{
	int ix, iz;
	if (calcTileLoc(x, z, &ix, &iz))
	{
		TileInfo& tile = getTile(ix, iz);
		rcHeightfield* heightField = tile.rc->solid;

		if (heightField)
		{
			return rcCheckHeightfieldCollision(x, ymin, ymax, z, *heightField);
		}
	}
	return false;
}


int BuildContext::getNearestHeight(const float x, const float y, const float z, float* h) const
{
	int ix, iz;
	if (calcTileLoc(x, z, &ix, &iz))
	{
		TileInfo& tile = getTile(ix, iz);
		rcHeightfield* heightField = tile.rc->solid;

		if (heightField)
			return rcGetNearestHeight(x, y, z, *heightField, h);
	}
	return false;
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

unsigned char countArea(unsigned char srcArea, bool autoOML, float norm, float walkableThr)
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
			area = RC_WALKABLE_AREA + ((srcArea << 1) + autoOML);
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

	config.cs = fmax(0.01f, settings.cellSize);

	// Height needs more precision so that walkableClimb is not too prone to rasterization errors.
	config.ch = config.cs * 0.5f;

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
	config.walkableRadius = fmax(0, (int)ceilf(settings.agentRadius / config.cs - config.cs * 0.01f));
	config.maxEdgeLen = 0;//(int)(settings.edgeMaxLen / settings.cellSize);
	config.maxSimplificationError = 1.3f;//settings.edgeMaxError;

	if (config.walkableClimb >= config.walkableHeight)
		config.walkableClimb = fmax(0, config.walkableHeight - 1);

	config.minRegionArea = (int)(settings.minRegionArea / (config.cs * config.cs)); //(int)rcSqr(settings.regionMinSize);		// Note: area = size*size
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

	isReading = false;

	m_talloc = new LinearAllocator(32 * 1024 * 1024);
	m_tcomp = new FastLZCompressor();
	m_tmproc = new MeshProcess();
	m_tmproc->m_nm = this;
	m_ctx = new BuildContext();
	m_ctx->m_nm = this;

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


	m_ctx->config = config;
	m_ctx->rcConfig = rcConfig;
	m_ctx->tileSize = data.tileSize;

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

	m_navQuery = dtAllocNavMeshQuery();

	if (!m_navQuery)
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not allocate dtAllocNavMeshQuery.");
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

	m_navQuery->init(m_navMesh, 4096);

	// Build initial meshes
	int64 start = Ctrl::getTimer();

	int sizeX = data.sizeX;
	int sizeZ = data.sizeZ;

	m_ctx->sizeX = sizeX;
	m_ctx->sizeZ = sizeZ;

	int dx;
	int dy;

	m_ctx->m_recastTiles = new TileInfo[sizeX * sizeZ];

	for (int y = -1; y <= sizeZ; ++y)
	{
		for (int x = -1; x <= sizeX; ++x)
		{
			dx = x + 1;
			dy = y + 1;

			if (m_ctx->inRange(dx, dy))
			{
				TileInfo& tile = m_ctx->getTile(dx, dy);
				tile.rc = new RasterizationContext();

				TileCacheData tiles[MAX_LAYERS];
				int nTiles = rasterizeTileLayers(data, dx, dy, rcConfig, tiles, MAX_LAYERS, *tile.rc);

				//if (nTiles == 0)
				//{
				//	/*m_ctx->log(RC_LOG_ERROR, "rasterizeTileLayers Failed.");
				//	clear();
				//	return;*/
				//	continue;
				//}

				TileInfo& tileInfo = m_ctx->getTile(dx, dy);
				tileInfo.ntiles = nTiles;

				for (int i = 0; i < nTiles; ++i)
				{
					TileCacheData* tile = &tiles[i];

					dtCompressedTileRef tileRef;
					status = m_tileCache->addTile(tile->data, tile->dataSize, DT_COMPRESSEDTILE_FREE_DATA, &tileRef);
					if (dtStatusFailed(status))
					{
						dtFree(tile->data);
						tile->data = 0;

						m_ctx->log(RC_LOG_ERROR, "addTileFailed.");
						clear();
						return;
					}

					status = m_tileCache->buildNavMeshTile(tileRef, m_navMesh);

					if (dtStatusFailed(status))
					{
						m_ctx->log(RC_LOG_ERROR, "buildTileFailed.");
						clear();
						return;
					}

					tileInfo.tileLayers[i].ref = m_navMesh->getTileRefAt(dx, dy, i);
				}
			}

			if (m_ctx->inRange(x, y))
			{
				appendOffMeshLinks(x, y);
			}

			dx = x - 1;
			dy = y - 1;

			//释放内存
			if (m_ctx->inRange(dx, dy))
			{
				m_ctx->clearTile(dx, dy);
			}
		}
	}


	//for (int y = 0; y < data.sizeZ; ++y)
	//{
	//	for (int x = 0; x < data.sizeX; ++x)
	//	{
	//		TileCacheData tiles[MAX_LAYERS];
	//		int nTiles = rasterizeTileLayers(data, x, y, rcConfig, tiles, MAX_LAYERS);

	//		for (int i = 0; i < nTiles; ++i)
	//		{
	//			TileCacheData* tile = &tiles[i];
	//			status = m_tileCache->addTile(tile->data, tile->dataSize, DT_COMPRESSEDTILE_FREE_DATA, 0);
	//			if (dtStatusFailed(status))
	//			{
	//				dtFree(tile->data);
	//				tile->data = 0;

	//				m_ctx->log(RC_LOG_ERROR, "addTileFailed.");
	//				clear();
	//				return;
	//			}

	//			//list.add(move(*tile));
	//			list.push_back(move(*tile));

	//			status = m_tileCache->buildNavMeshTilesAt(x, y, m_navMesh);

	//			if (dtStatusFailed(status))
	//			{
	//				m_ctx->log(RC_LOG_ERROR, "buildTileFailed.");
	//				clear();
	//				return;
	//			}
	//		}
	//	}
	//}

	int64 end = Ctrl::getTimer();

	print("cost time %d", end - start);

	save(savePath.c_str());

	clear();

	print("OK");
}


//void NavMesh::save(string path)
//{
//	if (!m_tileCache)
//		return;
//
//	FILE* fp = fopen(path.c_str(), "wb");
//
//	if (!fp)
//	{
//		Ctrl::errorLog("无法打开目录 %s", path.c_str());
//		return;
//	}
//
//	// Store header.
//	TileCacheSetHeader header;
//	header.magic = TILECACHESET_MAGIC;
//	header.version = TILECACHESET_VERSION;
//	header.numTiles = 0;
//	for (int i = 0; i < m_tileCache->getTileCount(); ++i)
//	{
//		const dtCompressedTile* tile = m_tileCache->getTile(i);
//		if (!tile || !tile->header || !tile->dataSize) continue;
//		header.numTiles++;
//	}
//	memcpy(&header.cacheParams, m_tileCache->getParams(), sizeof(dtTileCacheParams));
//	memcpy(&header.meshParams, m_navMesh->getParams(), sizeof(dtNavMeshParams));
//	fwrite(&header, sizeof(TileCacheSetHeader), 1, fp);
//
//	// Store tiles.
//	for (int i = 0; i < m_tileCache->getTileCount(); ++i)
//	{
//		const dtCompressedTile* tile = m_tileCache->getTile(i);
//		if (!tile || !tile->header || !tile->dataSize) continue;
//
//		TileCacheTileHeader tileHeader;
//		tileHeader.tileRef = m_tileCache->getTileRef(tile);
//		tileHeader.dataSize = tile->dataSize;
//		fwrite(&tileHeader, sizeof(tileHeader), 1, fp);
//
//		fwrite(tile->data, tile->dataSize, 1, fp);
//	}
//
//	fclose(fp);
//}

void NavMesh::save(string path)
{
	if (!m_tileCache)
		return;

	BytesWriteStream stream;

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

	stream.writeMem(&header, sizeof(TileCacheSetHeader));

	//OML
	int omlLen = m_OffMeshLinks.size();
	//ignore big/little Endian
	stream.writeMem(&omlLen, sizeof(int));

	for (AutoOffMeshLinkData v : m_OffMeshLinks)
	{
		stream.writeMem(&v, sizeof(AutoOffMeshLinkData));
	}

	// Store tiles.
	for (int i = 0; i < m_tileCache->getTileCount(); ++i)
	{
		const dtCompressedTile* tile = m_tileCache->getTile(i);
		if (!tile || !tile->header || !tile->dataSize) continue;

		TileCacheTileHeader tileHeader;
		tileHeader.tileRef = m_tileCache->getTileRef(tile);
		tileHeader.dataSize = tile->dataSize;

		stream.writeMem(&tileHeader, sizeof(tileHeader));
		stream.writeMem(tile->data, tile->dataSize);
	}

	FileUtils::writeFileForBytesWriteStream(path, stream);
}

void NavMesh::clear()
{
	isReading = false;

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

	if (m_navMesh)
	{
		delete m_navMesh;
		m_navMesh = nullptr;
	}

	if (m_navQuery)
	{
		delete m_navQuery;
		m_navQuery = nullptr;
	}

	m_OffMeshLinks.clear();

	clearOML();
}


void NavMesh::clearOML()
{
	offMeshConCount = 0;

	if (offMeshConVerts)
	{
		delete[] offMeshConVerts;
		offMeshConVerts = nullptr;
	}

	if (offMeshConRad)
	{
		delete[] offMeshConRad;
		offMeshConRad = nullptr;
	}

	if (offMeshConFlags)
	{
		delete[] offMeshConFlags;
		offMeshConFlags = nullptr;
	}

	if (offMeshConAreas)
	{
		delete[] offMeshConAreas;
		offMeshConAreas = nullptr;
	}

	if (offMeshConDir)
	{
		delete[] offMeshConDir;
		offMeshConDir = nullptr;
	}

	if (offMeshConUserID)
	{
		delete[] offMeshConUserID;
		offMeshConUserID = nullptr;
	}
}


void NavMesh::makeOML()
{
	offMeshConCount = m_OffMeshLinks.size();

	offMeshConVerts = new float[offMeshConCount * 6];
	offMeshConRad = new float[offMeshConCount];
	offMeshConFlags = new unsigned int[offMeshConCount];
	offMeshConAreas = new unsigned char[offMeshConCount];
	offMeshConDir = new unsigned char[offMeshConCount];
	offMeshConUserID = new unsigned int[offMeshConCount];

	for (int i = 0; i < offMeshConCount; i++)
	{
		AutoOffMeshLinkData v = m_OffMeshLinks[i];
		v.m_Start.toArray(&offMeshConVerts[i * 6]);
		v.m_End.toArray(&offMeshConVerts[i * 6 + 3]);
		offMeshConRad[i] = v.m_Radius;
		offMeshConFlags[i] = v.m_LinkType;
		offMeshConAreas[i] = v.m_Area;
		offMeshConDir[i] = v.m_LinkDirection;
		offMeshConUserID[i] = i;
	}

	m_OffMeshLinks.clear();
}

int NavMesh::rasterizeTileLayers(SceneRecastData& data, const int x, const int y, const rcConfig& cfg, TileCacheData* tiles, const int maxTiles, RasterizationContext& rc)
{
	FastLZCompressor comp;

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

	//
	//  Step 1. create height field 
	//

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

	/*recastTile.m_polyMesh = rcAllocPolyMesh();
	if (!recastTile.m_polyMesh)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not create solid rcAllocPolyMesh.");
		return 0;
	}

	recastTile.m_detailMesh = rcAllocPolyMeshDetail();
	if (!recastTile.m_detailMesh)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not create solid rcAllocPolyMeshDetail.");
		return 0;
	}*/


	/*recastTile.m_heightField = rcAllocHeightfield();
	if (!recastTile.m_heightField)
	{
		m_ctx->log(RC_LOG_ERROR, "buildNavigation: Could not create solid rcAllocHeightfield.");
		clear();
		return 0;
	}*/


	//
	// Step 2. Rasterize all geometry
	//

	const float walkableThr = cosf(tcfg.walkableSlopeAngle / 180.0f * RC_PI);
	unsigned char area = 0;
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
		unsigned char terrainFlag = RC_WALKABLE_AREA;// + 1

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
					area = terrainFlag;
				rcRasterizeTriangle(m_ctx, v0, v1, v3, area, *rc.solid);

				calcTriNormal(v3, v2, v0, norm);
				area = 0;
				if (norm[1] > walkableThr)
					area = terrainFlag;
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

				area = countArea((unsigned char)obj->area, obj->autoOML, norm[1], walkableThr);

				rcRasterizeTriangle(m_ctx, v0, v1, v2, area, *rc.solid);
			}
		}
	}

	//
   // Step 3. Filter walkables surfaces.
   //
	rcFilterForceUnwalkableArea(m_ctx, *rc.solid);
	rcFilterLowHangingWalkableObstacles(m_ctx, tcfg.walkableClimb, *rc.solid);
	rcFilterLedgeSpans(m_ctx, tcfg.walkableHeight, tcfg.walkableClimb, *rc.solid);
	rcFilterWalkableLowHeightSpans(m_ctx, tcfg.walkableHeight, *rc.solid);

	// Skip if tile is empty, i.e. it has no spans allocated
	/*if (*rc.solid->freelist == NULL)
		return 0;*/


		//
		// Step 4. Partition walkable surface to simple regions.
		//

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

	//
	// Step 5. Trace and simplify region contours.
	//

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
		return 0;
	}

	//
	// Step 6. Build polygons mesh from contours.
	//

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


void NavMesh::appendOffMeshLinks(const int x, const int z)
{
	vector<EdgeSegment> autoLinkEdgeSegments;
	getMeshEdges(x, z, autoLinkEdgeSegments);

	if (m_ctx->config.dropHeight > m_ctx->config.agentMaxClimb)
	{
		placeDropDownLinks(autoLinkEdgeSegments);
	}

	if (m_ctx->config.jumpDistance > m_ctx->config.agentRadius)
	{
		placeJumpAcrossLinks(autoLinkEdgeSegments);
	}
}

void NavMesh::getMeshEdges(int x, int z, vector<EdgeSegment>& autoLinkEdgeSegments)
{
	autoLinkEdgeSegments.clear();

	dtQueryFilter filter;

	const TileInfo& tileInfo = m_ctx->getTile(x, z);

	for (int i = 0; i < tileInfo.ntiles; ++i)
	{
		const TileLayerInfo& layerInfo = tileInfo.tileLayers[i];

		const dtMeshTile* tile = m_navMesh->getTileByRef(layerInfo.ref);

		if (tile)
		{
			const dtPolyRef base = m_navMesh->getPolyRefBase(tile);

			float segs[kMaxSegsPerPoly * 6];

			int polyCount = tile->header->polyCount;

			for (int ip = 0; ip < polyCount; ++ip)
			{
				if (layerInfo.autoOML[ip] == 0)
					continue;

				const dtPolyRef pRef = base | (dtPolyRef)ip;

				int nsegs;
				m_navQuery->getPolyWallSegments(pRef, &filter, segs, NULL, &nsegs, kMaxSegsPerPoly);

				float tempV[3];

				for (int k = 0; k < nsegs; ++k)
				{
					/*const float* start = &segs[6 * k];
					const float* end = &segs[6 * k + 3];

					dtVsub(tempV, start, end);
					tempV[1] = 0;
					dtVnormalize(tempV);

					const float tx = -tempV[2];
					const float tz = tempV[0];

					tempV[0] = tx;
					tempV[1] = 0;
					tempV[2] = tz;*/

					const Vector3 start = Vector3(&segs[6 * k]);
					const Vector3 end = Vector3(&segs[6 * k + 3]);

					Vector3 normal = start - end;
					normal.y = 0;
					normal = Normalize(normal);
					normal.Set(normal.z, 0, -normal.x);


					autoLinkEdgeSegments.emplace_back();
					EdgeSegment& segment = autoLinkEdgeSegments.back();
					segment.start = start;
					segment.end = end;
					segment.normal = normal;
				}
			}
		}
		else
		{
			//print("D1 tile null %d %d %d", x, z, i);
			//TODO:这里tile会为null
		}

	}

}


void NavMesh::placeDropDownLinks(vector<EdgeSegment>& autoLinkEdgeSegments)
{
	vector<AutoLinkPoints> autoLinkPoints;
	findValidDropDowns(autoLinkEdgeSegments, autoLinkPoints);

	m_OffMeshLinks.reserve(m_OffMeshLinks.size() + autoLinkPoints.size());

	for (size_t i = 0; i < autoLinkPoints.size(); ++i)
	{
		const Vector3& spos = autoLinkPoints[i].start;
		const Vector3& epos = autoLinkPoints[i].end;

		addOffMeshConnection(spos, epos, m_ctx->config.agentRadius, false, kJump, kLinkTypeDropDown);
	}
}


void NavMesh::findValidDropDowns(vector<EdgeSegment>& autoLinkEdgeSegments, vector<AutoLinkPoints>& autoLinkPoints)
{
	float agentRadius = m_ctx->rcConfig.walkableRadius * m_ctx->rcConfig.cs;
	float stepSize = agentRadius * 2;
	float dropDownOffset = m_ctx->config.agentRadius * 2 + m_ctx->rcConfig.cs * 4; // double agent radius + voxel error
	float minDropHeight = m_ctx->rcConfig.walkableClimb * m_ctx->rcConfig.ch;

	vector<Vector3> samplePositions;
	vector<Vector3> startPositions;

	for (size_t i = 0; i < autoLinkEdgeSegments.size(); i++)
	{
		const EdgeSegment& segment = autoLinkEdgeSegments[i];
		Vector3 offsetSegmentStart = segment.start + segment.normal * dropDownOffset;
		Vector3 offsetSegmentEnd = segment.end + segment.normal * dropDownOffset;

		getSubsampledLocations(segment.start, segment.end, stepSize, startPositions);
		getSubsampledLocations(offsetSegmentStart, offsetSegmentEnd, stepSize, samplePositions);

		for (size_t j = 0; j < samplePositions.size(); ++j)
		{
			float nearestDistance = verticalNavMeshTest(samplePositions[j], m_ctx->config.dropHeight);

			if (nearestDistance < m_ctx->config.dropHeight && nearestDistance > minDropHeight)
			{
				Vector3 endPosition(samplePositions[j]);
				endPosition.y -= nearestDistance;
				if (!dropDownBlocked(startPositions[j], endPosition, m_ctx->rcConfig.cs, m_ctx->rcConfig.ch))
				{
					autoLinkPoints.emplace_back();
					AutoLinkPoints& points = autoLinkPoints.back();

					points.start = startPositions[j];
					points.end = endPosition;
				}
			}
		}
	}
}


void NavMesh::getSubsampledLocations(Vector3 segmentStart, Vector3 segmentEnd, float subsampleDistance, vector<Vector3>& locations)
{
	locations.clear();

	float segmentLength = Magnitude(segmentStart - segmentEnd);
	Vector3 segmentMidPoint = (segmentStart + segmentEnd) / 2;
	Vector3 normal = segmentEnd - segmentStart;
	normal.y = 0;
	normal = Normalize(normal);
	normal.Set(normal.z, 0, -normal.x);

	locations.push_back(segmentMidPoint);
	if (segmentLength > subsampleDistance * 2) // construct subsample locations
	{
		Vector3 sampleStep = Normalize(segmentStart - segmentEnd) * subsampleDistance;
		Vector3 pos1 = segmentMidPoint;
		Vector3 pos2 = segmentMidPoint;
		float subSampleProgress = subsampleDistance;
		while (subSampleProgress < segmentLength / 2)
		{
			pos1 += sampleStep;
			pos2 -= sampleStep;
			locations.push_back(pos1);
			locations.push_back(pos2);
			subSampleProgress += subsampleDistance;
		}
	}
}


float NavMesh::verticalNavMeshTest(Vector3 testFrom, float testHeight)
{
	dtQueryFilter filter;
	float extentSize = testHeight * 0.5f;

	float extents[3];
	extents[0] = 0;
	extents[1] = extentSize;
	extents[2] = 0;

	Vector3 midPoint = testFrom;

	midPoint.y -= extentSize;
	dtPolyRef polys[128];
	int polyCount = 0;

	m_navQuery->queryPolygons(midPoint.getPtr(), extents, &filter, polys, &polyCount, 128);

	// Find higher poly but not higher then max height.
	float startHeight = testFrom.y;
	float nearestDistance = FLT_MAX;
	for (int polyIdx = 0; polyIdx < polyCount; ++polyIdx)
	{
		// TODO: Assumes local coords during baking
		float height;

		if (dtStatusSucceed(getPolyHeightLocal(polys[polyIdx], testFrom, &height)))
		{
			float distance = startHeight - height;
			if (distance > 0 && distance < nearestDistance)
			{
				nearestDistance = distance;
			}
		}
	}

	return nearestDistance;
}

bool NavMesh::dropDownBlocked(Vector3 startPos, Vector3 endPos, float cs, float ch)
{
	Vector3 xzStep, perp1Step, perp2Step, centerScanner, perp1Scanner, perp2Scanner;
	float height = m_ctx->rcConfig.walkableHeight * m_ctx->rcConfig.ch;
	float linkXZlength, scannedLen, radius, stepSize, verticalOffset;
	bool overedgeflag = false;
	float scanHeight;

	verticalOffset = (m_ctx->rcConfig.walkableClimb + 1) * ch;

	xzStep = endPos - startPos;
	xzStep.y = 0;
	linkXZlength = Magnitude(xzStep);
	xzStep = Normalize(xzStep);

	stepSize = cs / 2.0f;
	xzStep *= stepSize;
	perp1Step.x = -xzStep.z;
	perp1Step.y = 0;
	perp1Step.z = xzStep.x;
	perp2Step.x = xzStep.z;
	perp2Step.y = 0;
	perp2Step.z = -xzStep.x;

	radius = m_ctx->rcConfig.walkableRadius * cs;

	scannedLen = 0;
	centerScanner = startPos;
	centerScanner.y += verticalOffset;

	scanHeight = fmax(0.01f, height - verticalOffset);

	bool blocked = false;

	while (scannedLen < linkXZlength && !blocked)
	{
		if (m_ctx->checkHeightfieldCollision(centerScanner.x, centerScanner.y, centerScanner.y + scanHeight, centerScanner.z))
		{
			blocked = true;
			break;
		}
		float perpScannedLen = stepSize;
		perp1Scanner = centerScanner + perp1Step;
		perp2Scanner = centerScanner + perp2Step;
		while (perpScannedLen < radius)
		{
			if (m_ctx->checkHeightfieldCollision(perp1Scanner.x, perp1Scanner.y, perp1Scanner.y + scanHeight, perp1Scanner.z))
			{
				blocked = true;
				break;
			}

			if (m_ctx->checkHeightfieldCollision(perp2Scanner.x, perp2Scanner.y, perp2Scanner.y + scanHeight, perp2Scanner.z))
			{
				blocked = true;
				break;
			}
			perp1Scanner += perp1Step;
			perp2Scanner += perp2Step;
			perpScannedLen += stepSize;
		}

		scannedLen += stepSize;
		centerScanner += xzStep;

		if (!overedgeflag && (linkXZlength - scannedLen) < radius)
		{
			overedgeflag = true;
			centerScanner.y = endPos.y + verticalOffset;
			perp1Scanner.y = endPos.y + verticalOffset;
			perp2Scanner.y = endPos.y + verticalOffset;
			scanHeight += startPos.y - endPos.y;
		}
	}

	return blocked;
}


void NavMesh::placeJumpAcrossLinks(vector<EdgeSegment>& autoLinkEdgeSegments)
{
	vector<AutoLinkPoints> autoLinkPoints;
	findValidJumpAcrossLinks(autoLinkEdgeSegments, autoLinkPoints);

	m_OffMeshLinks.reserve(m_OffMeshLinks.size() + autoLinkPoints.size());

	for (size_t i = 0; i < autoLinkPoints.size(); ++i)
	{
		const Vector3& spos = autoLinkPoints[i].start;
		const Vector3& epos = autoLinkPoints[i].end;
		addOffMeshConnection(spos, epos, m_ctx->config.agentRadius, false, kJump, kLinkTypeJumpAcross);
	}
}


void NavMesh::findValidJumpAcrossLinks(vector<EdgeSegment>& autoLinkEdgeSegments, vector<AutoLinkPoints>& autoLinkPoints)
{
	float agentRadius = m_ctx->rcConfig.walkableRadius * m_ctx->rcConfig.cs;
	float unevenLinkMargin = m_ctx->rcConfig.ch * 2;
	float testDepth = unevenLinkMargin * 2.1f;
	float spacing = agentRadius * 2;
	float minimumJumpDistance = agentRadius * 2;
	float stepSize = m_ctx->rcConfig.cs;

	vector<Vector3> samplePositions;
	vector<Vector3> startPositions;

	// Clamp the scan distance for auto-link connections.
	// Since the offmeshlinks are processed as a 3x3 kernel we can use 2x tile diagonal.
	const float tileSize = m_ctx->rcConfig.tileSize * m_ctx->rcConfig.cs;
	const float sampleDistance = fmin(m_ctx->config.jumpDistance, 2.83f * tileSize);

	for (size_t i = 0; i < autoLinkEdgeSegments.size(); ++i)
	{
		const EdgeSegment& segment = autoLinkEdgeSegments[i];
		Vector3 offsetSegmentStart = segment.start + segment.normal * minimumJumpDistance;
		offsetSegmentStart.y += unevenLinkMargin;
		Vector3 offsetSegmentEnd = segment.end + segment.normal * minimumJumpDistance;
		offsetSegmentEnd.y += unevenLinkMargin;

		getSubsampledLocations(segment.start, segment.end, spacing, startPositions);
		getSubsampledLocations(offsetSegmentStart, offsetSegmentEnd, spacing, samplePositions);

		Vector3 sampleStep = segment.normal * stepSize;
		for (size_t j = 0; j < samplePositions.size(); ++j)
		{
			float sampleProgress = 0;
			Vector3 samplePoint = samplePositions[j];

			while (sampleProgress < sampleDistance)
			{
				// Use heightfield first to sample a potential landing spot.
				float h = 0;
				if (m_ctx->getNearestHeight(samplePoint.x, samplePoint.y, samplePoint.z, &h))
				{
					float distanceToGround = std::abs(h - samplePoint.y);

					if (distanceToGround <= testDepth)
					{
						// If the landing spot is close to navmesh, accept it.
						float nearestDistance = verticalNavMeshTest(samplePoint, testDepth);
						if (nearestDistance <= testDepth)
						{
							Vector3 endPosition(samplePoint);
							endPosition.y -= nearestDistance;

							bool blocked = jumpAcrossBlocked(startPositions[j], endPosition, m_ctx->rcConfig.cs, m_ctx->rcConfig.ch);
							if (!blocked)
							{
								autoLinkPoints.emplace_back();
								AutoLinkPoints& points = autoLinkPoints.back();
								points.start = startPositions[j];
								points.end = endPosition;
							}

							break;
						}
					}
				}

				sampleProgress += stepSize;
				samplePoint += sampleStep;
			}
		}
	}
}


bool NavMesh::jumpAcrossBlocked(Vector3 startPos, Vector3 endPos, float cs, float ch)
{
	Vector3 xzStep, perp1Step, perp2Step, centerScanner, perp1Scanner, perp2Scanner;
	float height = m_ctx->rcConfig.walkableHeight * m_ctx->rcConfig.ch;
	float linkXZlength, scannedLen, radius, stepSize, verticalOffset;
	float scanHeight;
	float heightDiff = std::abs(startPos.y - endPos.y);

	verticalOffset = (m_ctx->rcConfig.walkableClimb + 1) * ch + heightDiff;

	xzStep = endPos - startPos;
	xzStep.y = 0;

	linkXZlength = Magnitude(xzStep);
	xzStep = Normalize(xzStep);

	stepSize = cs / 2.0f;
	xzStep *= stepSize;
	perp1Step.x = xzStep.z;
	perp1Step.y = 0;
	perp1Step.z = -xzStep.x;
	perp2Step.x = -xzStep.z;
	perp2Step.y = 0;
	perp2Step.z = xzStep.x;

	radius = m_ctx->rcConfig.walkableRadius * cs;

	scannedLen = 0;
	centerScanner = startPos;
	centerScanner.y += verticalOffset;
	scanHeight = fmax(0.01f, height - verticalOffset);

	bool blocked = false;

	while (scannedLen < linkXZlength && !blocked)
	{
		if (m_ctx->checkHeightfieldCollision(centerScanner.x, centerScanner.y, centerScanner.y + scanHeight, centerScanner.z))
		{
			blocked = true;
			break;
		}
		float perpScannedLen = stepSize;
		perp1Scanner = centerScanner + perp1Step;
		perp2Scanner = centerScanner + perp2Step;
		while (perpScannedLen < radius)
		{
			if (m_ctx->checkHeightfieldCollision(perp1Scanner.x, perp1Scanner.y, perp1Scanner.y + scanHeight, perp1Scanner.z))
			{
				blocked = true;
				break;
			}
			if (m_ctx->checkHeightfieldCollision(perp2Scanner.x, perp2Scanner.y, perp2Scanner.y + scanHeight, perp2Scanner.z))
			{
				blocked = true;
				break;
			}
			perp1Scanner += perp1Step;
			perp2Scanner += perp2Step;
			perpScannedLen += stepSize;
		}

		scannedLen += stepSize;
		centerScanner += xzStep;
	}

	return blocked;
}

static inline unsigned int getPolyIndex(const dtMeshTile* tile, const dtPoly* poly)
{
	dtAssert(poly);
	dtAssert(tile);
	dtAssert(tile->header);
	const unsigned int ip = (unsigned int)(poly - tile->polys);
	dtAssert(ip < tile->header->polyCount);
	return ip;
}

static bool closestHeightPointTriangle(float* h, const float* p, const float* a, const float* b, const float* c)
{
	float v0[3];
	float v1[3];
	float v2[3];

	dtVsub(v0, c, a);
	dtVsub(v1, b, a);
	dtVsub(v2, p, a);

	const float dot00 = dtVdot2D(v0, v0);
	const float dot01 = dtVdot2D(v0, v1);
	const float dot02 = dtVdot2D(v0, v2);
	const float dot11 = dtVdot2D(v1, v1);
	const float dot12 = dtVdot2D(v1, v2);

	// Compute barycentric coordinates
	const float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
	const float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
	const float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

	// The (sloppy) epsilon is needed to allow to get height of points which
	// are interpolated along the edges of the triangles.
	static const float EPS = 1e-4f;

	// If point lies inside the triangle, return interpolated ycoord.
	if (u >= -EPS && v >= -EPS && (u + v) <= 1.0f + EPS)
	{
		*h = a[1] + v0[1] * u + v1[1] * v;
		return true;
	}

	return false;
}

static bool projectToPolyDetail(const dtMeshTile* tile, const dtPoly* poly, const Vector3& pos, float* height)
{
	const unsigned int ip = getPolyIndex(tile, poly);

	const dtPolyDetail* pd = &tile->detailMeshes[ip];
	for (int j = 0; j < pd->triCount; ++j)
	{
		const unsigned char* t = &tile->detailTris[(pd->triBase + j) * 4];
		const float* v[3];
		for (int k = 0; k < 3; ++k)
		{
			if (t[k] < poly->vertCount)
				v[k] = &tile->verts[poly->verts[t[k]] * 3];
			else
				v[k] = &tile->detailVerts[(pd->vertBase + (t[k] - poly->vertCount)) * 3];
		}

		float h;
		if (closestHeightPointTriangle(&h, pos.getPtr(), v[0], v[1], v[2]))
		{
			*height = h;
			return true;
		}
	}
	return false;
}

float sqrDistancePointSegment2D(float* t, const Vector3& pt, const Vector3& s1, const Vector3& s2)
{
	const float dsx = s2.x - s1.x;
	const float dsz = s2.z - s1.z;
	const float dpx = pt.x - s1.x;
	const float dpz = pt.z - s1.z;
	const float den = dsx * dsx + dsz * dsz;
	if (den == 0)
	{
		*t = 0;
		return dpx * dpx + dpz * dpz;
	}
	float tt = (dsx * dpx + dsz * dpz) / den;
	tt = MathUtils::clampf(tt, 0.0f, 1.0f);
	const float x = tt * dsx - dpx;
	const float z = tt * dsz - dpz;
	*t = tt;
	return x * x + z * z;
}

// In the plane of projection find the height of the closest point on the edge of the detail triangles
static float projectToPolyDetailEdge(const dtMeshTile* tile, const dtPoly* poly, const Vector3& pos)
{
	const unsigned int ip = getPolyIndex(tile, poly);
	const dtPolyDetail* pd = &tile->detailMeshes[ip];

	float dmin = FLT_MAX;
	float h = FLT_MAX;
	for (int j = 0; j < pd->triCount; ++j)
	{
		const unsigned char* t = &tile->detailTris[(pd->triBase + j) * 4];
		Vector3 vec[3];
		for (int k = 0; k < 3; ++k)
		{
			if (t[k] < poly->vertCount)
				vec[k] = Vector3(&tile->verts[poly->verts[t[k]] * 3]);
			else
				vec[k] = Vector3(&tile->detailVerts[(pd->vertBase + (t[k] - poly->vertCount)) * 3]);
		}


		for (int kp = 2, k = 0; k < 3; kp = k++)
		{
			float tt;
			float d = sqrDistancePointSegment2D(&tt, pos, vec[kp], vec[k]);
			if (d < dmin)
			{
				dmin = d;
				h = MathUtils::lerpf(vec[kp].y, vec[k].y, tt);
			}
		}
	}
	return h;
}

dtStatus NavMesh::getPolyHeightLocal(dtPolyRef ref, const Vector3& pos, float* height)
{
	dtAssert(height);

	const dtMeshTile* tile;
	const dtPoly* poly;

	if (dtStatusSucceed(m_navMesh->getTileAndPolyByRef(ref, &tile, &poly)))
	{
		if (poly->getType() == dtPolyTypes::DT_POLYTYPE_OFFMESH_CONNECTION)
		{
			const dtOffMeshConnection* con = m_navMesh->getOffMeshConnectionByRef(ref);

			if (con)
			{
				const float* v0 = &con->pos[0];
				const float* v1 = &con->pos[3];

				const float d0 = dtVdist(pos.getPtr(), v0);
				const float d1 = dtVdist(pos.getPtr(), v1);

				const float u = d0 / (d0 + d1);

				*height = MathUtils::lerpf(v0[1], v1[1], u);
				return DT_SUCCESS;
			}
		}
		else
		{
			if (projectToPolyDetail(tile, poly, pos, height))
				return DT_SUCCESS;

			*height = projectToPolyDetailEdge(tile, poly, pos);
			return DT_SUCCESS;
		}
	}

	return DT_FAILURE | DT_INVALID_PARAM;
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

	isReading = true;

	m_talloc = new LinearAllocator(32 * 1024 * 1024);
	m_tcomp = new FastLZCompressor();
	m_tmproc = new MeshProcess();
	m_tmproc->m_nm = this;

	status = m_tileCache->init(&header.cacheParams, m_talloc, m_tcomp, m_tmproc);
	if (dtStatusFailed(status))
	{
		Ctrl::errorLog("read version 出错");
		clear();
		return;
	}

	m_OffMeshLinks.clear();
	int omlLen;
	stream.readMem(&omlLen, sizeof(int));

	//OML
	for (int i = 0; i < omlLen; ++i)
	{
		AutoOffMeshLinkData v;
		stream.readMem(&v, sizeof(AutoOffMeshLinkData));
		m_OffMeshLinks.push_back(v);
	}

	makeOML();

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

	if (!m_navQuery)
	{
		m_ctx->log(RC_LOG_ERROR, "buildTiledNavigation: Could not allocate dtAllocNavMeshQuery.");
		clear();
		return;
	}

	m_navQuery->init(m_navMesh, 4096);
	//m_crowd = dtAllocCrowd();
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

inline bool overlapRange(const float amin, const float amax, const float bmin, const float bmax)
{
	return (amin >= bmax || amax <= bmin) ? false : true;
}

bool rcCheckHeightfieldCollision(const float x, const float ymin, const float ymax, const float z, const rcHeightfield& hf)
{
	const int w = hf.width;
	const int h = hf.height;
	const float cs = hf.cs;
	const float ch = hf.ch;
	const float* orig = hf.bmin;
	const int ix = (int)floorf((x - orig[0]) / cs);
	const int iz = (int)floorf((z - orig[2]) / cs);

	if (ix < 0 || iz < 0 || ix >= w || iz >= h)
		return false;

	const rcSpan* s = hf.spans[ix + iz * w];
	if (!s) return false;

	while (s)
	{
		const float symin = orig[1] + s->smin * ch;
		const float symax = orig[1] + s->smax * ch;
		if (overlapRange(ymin, ymax, symin, symax))
		{
			return true;
		}
		s = s->next;
	}

	return false;
}


bool rcGetNearestHeight(const float x, const float y, const float z, const rcHeightfield& hf, float* height)
{
	const int w = hf.width;
	const int h = hf.height;
	const float cs = hf.cs;
	const float ch = hf.ch;
	const float* orig = hf.bmin;
	const int ix = (int)floorf((x - orig[0]) / cs);
	const int iz = (int)floorf((z - orig[2]) / cs);

	if (ix < 0 || iz < 0 || ix >= w || iz >= h)
		return false;

	const rcSpan* s = hf.spans[ix + iz * w];
	if (!s) return false;

	float dmin = FLT_MAX;
	while (s)
	{
		const float symax = orig[1] + s->smax * ch;
		const float d = rcAbs(y - symax);
		if (symax < y && d < dmin)
		{
			dmin = d;
			*height = symax;
		}

		s = s->next;
	}

	return dmin < FLT_MAX;
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

	unsigned int flag;
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
			for (size_t i = 0; i < path->pointNum; i++)
			{
				path->areas[i] = 0;
			}

			return false;
		}

		for (size_t i = 0; i < path->pointNum; i++)
		{
			if (straightPathRefs[i] == 0)
				path->areas[i] = 0;
			else
				m_navMesh->getPolyArea(straightPathRefs[i], &path->areas[i]);
		}

		return true;
	}

	return false;
}


void NavMesh::addOffMeshConnection(const Vector3& start, const Vector3& end, const float radius, bool bidirectional, unsigned char area, OffMeshLinkType linkType)
{
	dtAssert(area < 32);

	//m_OffMeshLinks.push_back(AutoOffMeshLinkData());
	m_OffMeshLinks.emplace_back();
	AutoOffMeshLinkData& link = m_OffMeshLinks.back();
	link.m_Start = start;
	link.m_End = end;
	link.m_Radius = radius;
	link.m_LinkType = (unsigned short)linkType;
	link.m_Area = area;
	link.m_LinkDirection = bidirectional ? kLinkDirectionTwoWay : kLinkDirectionOneWay;
}

void NavMeshConfig::read(BytesReadStream* stream)
{
	agentHeight = stream->readFloat();
	agentRadius = stream->readFloat();
	agentMaxClimb = stream->readFloat();
	agentMaxSlope = stream->readFloat();

	dropHeight = stream->readFloat();
	jumpDistance = stream->readFloat();

	tileSize = stream->readFloat();
	cellSize = stream->readFloat();

	minRegionArea = stream->readFloat();
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
	readVec3(stream, x);
	readVec3(stream, y);
	readVec3(stream, z);
	readVec3(stream, w);
	readVec3(stream, min);
	readVec3(stream, max);
	area = stream->readInt();
	autoOML = stream->readBoolean();
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
