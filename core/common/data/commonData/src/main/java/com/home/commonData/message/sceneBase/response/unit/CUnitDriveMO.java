package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.DriveDO;
import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

/** 单位驾驶消息 */
public class CUnitDriveMO extends CUnitRMO
{
	/** 当前位置 */
	@MaybeNull
	PosDirDO nowPos;
	/** 驾驶数据 */
	DriveDO data;
}
