package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;

/** 客户端单位停止移动消息 */
public class CUnitStopMoveMO extends CUnitRMO
{
	/** 停止的客户端位置 */
	PosDirDO posDir;
}
