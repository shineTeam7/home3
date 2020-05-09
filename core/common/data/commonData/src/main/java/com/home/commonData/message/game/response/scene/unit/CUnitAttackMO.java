package com.home.commonData.message.game.response.scene.unit;

import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.commonData.message.game.response.scene.base.CUnitRMO;
import com.home.shineData.support.MessageDontCopy;

import java.util.List;

/** 控制单位使用Attack(客户端驱动attack时用) */
@MessageDontCopy
public class CUnitAttackMO extends CUnitRMO
{
	/** 攻击ID */
	int attackID;
	/** 攻击等级 */
	int attackLevel;
	/** 目标数据 */
	SkillTargetDO targetData;
	/** 目标组 */
	List<Integer> targets;
	/** 是否子弹第一次攻击 */
	boolean isBulletFirstHit;
}
