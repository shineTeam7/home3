package com.home.commonData.data.scene.unit;

import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.shineData.support.MaybeNull;

/** 附加战斗数据 */
public class UnitFightExDO
{
	/** 当前技能ID(-1为没有释放技能) */
	int currentSkillID=-1;
	/** 当前技能等级 */
	int currentSkillLevel=0;
	/** 当前技能目标 */
	@MaybeNull
	SkillTargetDO currentTarget;
	/** 当前技能步 */
	int currentSkillStep;
	/** 当前步时间经过 */
	int currentSkillStepTimePass;
	
	/** 当前技能读条ID */
	int currentSkillBarID=-1;
	/** 当前技能读条时间经过 */
	int currentSkillBarTimePass;
}
