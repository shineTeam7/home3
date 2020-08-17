package com.home.commonData.data.scene.scene;

import com.home.commonData.data.scene.role.SceneRoleDO;
import com.home.commonData.data.scene.unit.UnitDO;
import com.home.commonData.data.scene.unit.UnitSimpleDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.MaybeNull;

import java.util.List;
import java.util.Map;

/** 场景进入数据 */
public class SceneEnterDO
{
	/** 主角(可为空) */
	@MaybeNull
	UnitDO hero;
	/** 单位数据 */
	List<UnitDO> units;
	/** 场景角色组(副本类有效) */
	@MapKeyInValue("playerID")
	Map<Long,SceneRoleDO> roles;
	/** 自身绑定掉落包组 */
	@MaybeNull
	@MapKeyInValue("instanceID")
	Map<Integer,FieldItemBagBindDO> selfBindFieldItemBags;
	/** 视野绑定单位数据 */
	@MaybeNull
	Map<Integer,UnitSimpleDO> bindVisionUnits;
	/** 副本数据 */
	@MaybeNull
	BattleSceneDO battleData;
}
