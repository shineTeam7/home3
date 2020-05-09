package com.home.commonData.message.game.response.scene.unit;

import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.commonData.message.game.response.scene.base.CUnitRMO;

/** 客户端控制单位子弹命中 */
public class CUnitBulletHitMO extends CUnitRMO
{
	/** 子弹ID */
	int bulletID;
	/** 子弹等级 */
	int bulletLevel;
	/** 目标 */
	SkillTargetDO target;
}
