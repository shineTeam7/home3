package com.home.commonData.message.sceneBase.request.unit.building;

import com.home.commonData.message.sceneBase.request.base.UnitSMO;

/** 建筑升级成功的广播协议 注意:包含父类参数 instanceID */
public class BuildingLevelUpingCompleteMO extends UnitSMO
{
	/** 下一等级 */
	int level;
}
