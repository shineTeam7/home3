package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.fight.DamageOneDO;
import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.commonData.message.sceneBase.request.base.SceneSMO;
import com.home.shineData.support.MessageDontCopy;

/** 攻击伤害单个数据() */
@MessageDontCopy
public class AttackDamageOneMO extends SceneSMO
{
	/** 来源单位 */
	int fromInstanceID;
	/** 目标数据 */
	SkillTargetDO target;
	/** 攻击ID */
	int id;
	/** 攻击等级 */
	int level;
	/** 伤害数据 */
	DamageOneDO data;
}
