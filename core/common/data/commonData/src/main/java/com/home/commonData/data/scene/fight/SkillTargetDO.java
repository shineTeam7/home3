package com.home.commonData.data.scene.fight;

import com.home.commonData.data.scene.base.DirDO;
import com.home.commonData.data.scene.base.PosDO;
import com.home.shineData.support.MaybeNull;

/** 技能目标数据 */
public class SkillTargetDO
{
	/** 目标类型 */
	int type;
	/** 目标流水ID */
	int targetInstanceID=-1;
	/** 目标位置 */
	@MaybeNull
	PosDO pos;
	/** 目标朝向 */
	@MaybeNull
	DirDO dir;
	/** 携带参数 */
	int arg;
}
