#pragma once
#include "SInclude.h"
#include "../control/GameFactoryControl.h"
#include "../control/SceneControl.h"

/** 单例中心 */
class GameC
{
public:
	/** 工厂控制 */
	static GameFactoryControl* factory;
	/** 场景控制 */
	static SceneControl* scene;
};
