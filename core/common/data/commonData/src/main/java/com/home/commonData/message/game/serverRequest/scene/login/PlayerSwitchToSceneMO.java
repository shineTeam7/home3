package com.home.commonData.message.game.serverRequest.scene.login;

import com.home.commonData.data.role.RoleShowDO;
import com.home.commonData.data.scene.scene.SceneEnterArgDO;
import com.home.commonData.message.game.serverRequest.scene.base.PlayerGameToSceneMO;
import com.home.shineData.support.MaybeNull;

/** 玩家登录到场景服消息 */
public class PlayerSwitchToSceneMO
{
	long playerID;
	/** 外显数 */
	RoleShowDO showData;
	/** 场景进入数据 */
	SceneEnterArgDO enterArg;
}
