#include "SceneControl.h"

void SceneControl::init()
{

}

void SceneControl::registMap(int mapID, char* bytes, int size)
{
	_sceneBytesDic->put(mapID, { bytes, size });
}

Scene* SceneControl::createScene(int mapID)
{
	if(!_sceneBytesDic->contains(mapID))
		return nullptr;

	pair<char*, int> p = _sceneBytesDic->get(mapID);

	Scene* scene = new Scene();
	scene->init(mapID, p.first, p.second);

	return scene;
}
