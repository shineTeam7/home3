package com.home.commonData.message.center.serverRequest.game.scene;

import com.home.commonData.data.scene.scene.SceneLocationDO;
import com.home.commonData.message.center.serverRequest.game.base.PlayerToGameMO;

/** 进入指定场景到game */
public class EnterSignedSceneToGameMO extends PlayerToGameMO
{
	/** 场景位置数据 */
	SceneLocationDO data;
}
