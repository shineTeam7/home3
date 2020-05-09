#pragma once
#include "SInclude.h"
#include "../navmesh/NavMesh.h"

/** 场景 */
class Scene
{
private:
	NavMesh* _nav = nullptr;
	void clear();

public:
	~Scene();
	/** 地图id */
	int mapID;

	/** 初始化 */
	void init(int mapID, char* bytes, int len);

	NavMesh* getNavMesh()
	{
		return _nav;
	}
};
