package com.home.commonData.data.scene.unit;

import com.home.commonData.data.scene.base.BuffDO;
import com.home.commonData.data.scene.base.CDDO;
import com.home.commonData.data.scene.base.SkillDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 战斗基础数据(4元素+cd) */
public class UnitFightDO
{
	/** 状态组 */
	Map<Integer,Boolean> status;
	/** 属性组 */
	Map<Integer,Integer> attributes;
	/** 技能组 */
	@MapKeyInValue("id")
	Map<Integer,SkillDO> skills;
	/** buff组 */
	@MapKeyInValue("instanceID")
	Map<Integer,BuffDO> buffs;
	/** CD组 */
	@MapKeyInValue("id")
	Map<Integer,CDDO> cds;
}
