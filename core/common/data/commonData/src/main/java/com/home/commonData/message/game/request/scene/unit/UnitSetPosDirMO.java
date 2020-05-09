package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.game.request.scene.base.UnitSMO;

/** 单位直接更改位置朝向消息 */
public class UnitSetPosDirMO extends UnitSMO
{
	/** 位置朝向 */
	PosDirDO posDir;
}
