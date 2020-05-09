package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.game.request.scene.base.UnitSMO;
import com.home.shineData.support.MaybeNull;

/** 单位特殊移动消息 */
public class UnitSpecialMoveMO extends UnitSMO
{
	/** 特殊移动ID */
	int id;
	/** 当前位置 */
	PosDirDO posDir;
	/** 参数组 */
	@MaybeNull
	int[] args;
	/** 特殊移动剩余时间 */
	int specialMoveLastTime;
	/** 基元移动点位置 */
	@MaybeNull
	PosDO baseMovePos;
}
