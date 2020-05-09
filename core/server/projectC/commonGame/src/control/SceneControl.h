#pragma once
#include "SInclude.h"
#include "support/collection/SMap.h"
#include "../scene/Scene.h"

/** 场景控制 */
class SceneControl
{
private:
	/** 场景数据字典 */
	SMap<int, pair<char*, int>>* _sceneBytesDic = new SMap<int, pair<char*, int>>();

public:
	virtual void init();
	/** 注册地图 */
	void registMap(int mapID, char* bytes, int size);
	/** 创建场景 */
	Scene* createScene(int mapID);
};
