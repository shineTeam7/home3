#pragma once
#include "SInclude.h"
#include "SceneControl.h"

class GameFactoryControl
{
public:
	virtual SceneControl* createSceneControl()
	{
		return new SceneControl();
	}
};
