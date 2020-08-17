package com.home.commonData.message.game.serverRequest.scene.login;

import com.home.commonData.data.scene.scene.SceneServerEnterDO;
import com.home.commonData.message.game.serverRequest.scene.base.PlayerGameToSceneMO;

/** 玩家进入场景服消息 */
public class PlayerEnterServerSceneMO extends PlayerGameToSceneMO
{
	/** 携带数据 */
	SceneServerEnterDO data;
}
