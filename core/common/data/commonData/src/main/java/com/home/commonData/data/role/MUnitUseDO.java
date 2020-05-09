package com.home.commonData.data.role;

import com.home.commonData.data.item.EquipContainerDO;
import com.home.commonData.data.scene.unit.UnitAvatarDO;
import com.home.commonData.data.scene.unit.UnitFightDO;

/** 主单位(可脱离场景存在的)使用数据 */
public class MUnitUseDO
{
	/** 单位id */
	int id;
	/** 等级 */
	int level;
	/** 造型数据 */
	UnitAvatarDO avatar;
	/** 战斗数据 */
	UnitFightDO fight;
	/** 装备组 */
	EquipContainerDO equips;
	/** 主单位序号 */
	int mIndex;
	/** 是否上阵 */
	boolean isWorking;
}
