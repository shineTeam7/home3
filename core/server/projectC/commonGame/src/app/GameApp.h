#pragma once
#include "SInclude.h"
#include "../control/GameFactoryControl.h"

class GameApp
{
public:
	/** 启动 */
	void start();

protected:
	virtual void preInit();
	virtual void makeControls();
	virtual void onStartNext();

	virtual GameFactoryControl* createFactoryControl();

	GameFactoryControl* _factory = nullptr;
private:
	bool _inited = false;
};
