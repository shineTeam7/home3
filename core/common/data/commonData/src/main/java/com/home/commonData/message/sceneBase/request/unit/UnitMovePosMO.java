package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.sceneBase.request.base.UnitSMO;

/** 单位移动位置 */
public class UnitMovePosMO extends UnitSMO
{
	/** 移动类型 */
	int type;
	/** 目标位置 */
	PosDO targetPos;
	/** 服务器首点移动时间(同步用) */
	int moveTime;
}
