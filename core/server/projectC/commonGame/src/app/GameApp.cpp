#include "GameApp.h"
#include "../global/GameC.h"

void GameApp::start()
{
	preInit();


}

void GameApp::preInit()
{
	_inited = true;

	makeControls();

	onStartNext();
}

void GameApp::makeControls()
{
	GameFactoryControl* factory;
	_factory = factory = GameC::factory = createFactoryControl();
	GameC::scene = factory->createSceneControl();
}


void GameApp::onStartNext()
{
	GameC::scene->init();
}

GameFactoryControl* GameApp::createFactoryControl()
{
	return new GameFactoryControl();
}
