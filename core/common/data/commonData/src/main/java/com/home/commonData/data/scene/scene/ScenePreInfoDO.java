package com.home.commonData.data.scene.scene;

import com.home.commonData.data.role.RoleShowDO;
import com.home.shineData.support.MaybeNull;

/** 场景预备信息数据 */
public class ScenePreInfoDO
{
	/** 指定玩家组 */
	@MaybeNull
	RoleShowDO[] signedPlayers;
}
