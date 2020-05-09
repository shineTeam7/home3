package com.home.commonData.player.server;

import com.home.commonData.data.role.MUnitCacheDO;
import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.data.scene.scene.SceneEnterArgDO;
import com.home.shineData.support.MaybeNull;

/** 场景模块 */
public class SceneSPO
{
	/** 上次所在主城ID */
	int lastTownID=-1;
	/** 上次所在主城位置 */
	@MaybeNull
	PosDirDO lastTownPosDir;
	/** 所选线ID */
	int lineID=-1;
	
	/** 当前场景进入参数数据 */
	@MaybeNull
	SceneEnterArgDO currentSceneEnterArg;
	/** 当前场景位置 */
	@MaybeNull
	PosDirDO currentScenePosDir;
	/** 主城角色保存数据 */
	@MaybeNull
	MUnitCacheDO lastTownSaveData;
}
