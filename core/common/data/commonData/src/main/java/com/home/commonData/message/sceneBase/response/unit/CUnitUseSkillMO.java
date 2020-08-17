package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.data.scene.fight.SkillTargetDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;

/** 玩家单位使用技能 */
public class CUnitUseSkillMO extends CUnitRMO
{
	/** 技能ID */
	int skillID;
	/** 目标数据 */
	SkillTargetDO targetData;
	/** 当前单位位置数据 */
	PosDirDO posDir;
	/** 是否强制当前技能 */
	boolean isSuspend;
}
