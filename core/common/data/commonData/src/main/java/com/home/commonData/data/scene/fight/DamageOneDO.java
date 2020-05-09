package com.home.commonData.data.scene.fight;

import java.util.List;

import com.home.shineData.data.DIntDO;

/** 单个伤害数据 */
public class DamageOneDO
{
	/** 单位实例ID */
	int instanceID;
	/** 是否命中 */
	boolean isHit;
	/** 是否暴击 */
	boolean isCrit;
	/** 伤害组(伤害类型:伤害值) */
	List<DIntDO> damages;
	/** 是否造成击杀 */
	boolean isKilled;
	/** 伤害参数(表现用,如背击) */
	int arg;
}
