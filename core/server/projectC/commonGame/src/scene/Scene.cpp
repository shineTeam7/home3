#include "Scene.h"
#include "../navmesh/NavMesh.h"


Scene::~Scene()
{
	clear();
}

void Scene::clear()
{

}

void Scene::init(int mapID, char* bytes, int len)
{
	this->mapID = mapID;

	_nav = new NavMesh();
	_nav->readBytes(bytes, len);
}
