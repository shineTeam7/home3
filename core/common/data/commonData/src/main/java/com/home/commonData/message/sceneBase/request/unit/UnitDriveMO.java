package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.DriveDO;
import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.sceneBase.request.base.UnitSMO;
import com.home.shineData.support.MaybeNull;

public class UnitDriveMO extends UnitSMO
{
	/** 当前位置 */
	@MaybeNull
	PosDirDO nowPos;
	/** 驾驶数据 */
	DriveDO data;
}
