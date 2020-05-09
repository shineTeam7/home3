package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.commonData.message.game.request.scene.base.UnitSMO;

/** 单位使用技能 */
public class UnitUseSkillMO extends UnitSMO
{
	/** 技能ID */
	int skillID;
	/** 技能等级 */
	int skillLevel;
	/** 目标数据 */
	SkillTargetDO targetData;
	/** 当前单位位置数据 */
	PosDirDO posDir;
}
