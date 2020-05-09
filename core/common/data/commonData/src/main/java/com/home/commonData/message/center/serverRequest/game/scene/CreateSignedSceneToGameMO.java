package com.home.commonData.message.center.serverRequest.game.scene;

import com.home.commonData.data.role.RoleShowDO;
import com.home.commonData.data.scene.scene.CreateSceneDO;
import com.home.shineData.support.MessageDontCopy;

/** 创建指定场景到Game服 */
@MessageDontCopy
public class CreateSignedSceneToGameMO
{
	/** 事务索引 */
	int index;
	/** 创建场景数据 */
	CreateSceneDO createData;
	/** 指定角色组 */
	RoleShowDO[] signedPlayers;
}
