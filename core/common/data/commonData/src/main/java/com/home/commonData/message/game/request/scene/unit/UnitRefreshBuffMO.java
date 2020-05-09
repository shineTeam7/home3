package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.message.game.request.scene.base.UnitSMO;

/** 单位刷新buff */
public class UnitRefreshBuffMO extends UnitSMO
{
	/** buff实例ID */
	int buffInstanceID;
	/** 剩余时间 */
	int lastTime;
	/** 剩余次数 */
	int lastNum;
}
