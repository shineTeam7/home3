package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.CDDO;
import com.home.commonData.message.sceneBase.request.base.UnitSMO;

import java.util.List;

/** 单位开始CD组 */
public class UnitStartCDsMO extends UnitSMO
{
	/** 冷却组 */
	List<CDDO> cds;
}
