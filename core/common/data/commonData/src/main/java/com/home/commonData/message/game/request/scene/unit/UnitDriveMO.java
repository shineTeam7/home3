package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.data.scene.base.DriveDO;
import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.game.request.scene.base.UnitSMO;
import com.home.shineData.support.MaybeNull;

public class UnitDriveMO extends UnitSMO
{
	/** 当前位置 */
	@MaybeNull
	PosDirDO nowPos;
	/** 驾驶数据 */
	DriveDO data;
}
