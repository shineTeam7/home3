package com.home.commonData.message.scene.serverRequest.game.login;

import com.home.commonData.data.scene.scene.SceneServerExitDO;
import com.home.commonData.message.scene.serverRequest.game.base.PlayerSceneToGameMO;

/** 玩家离开场景完毕到game服 */
public class PlayerLeaveSceneOverToGameMO extends PlayerSceneToGameMO
{
	/** 携带数据 */
	SceneServerExitDO data;
}
