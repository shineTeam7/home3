package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

/** 客户端单位移动到点消息 */
public class CUnitMovePosMO extends CUnitRMO
{
	/** 移动类型 */
	int type;
	/** 当前位置 */
	@MaybeNull
	PosDO nowPos;
	/** 目标位置 */
	PosDO targetPos;
}
