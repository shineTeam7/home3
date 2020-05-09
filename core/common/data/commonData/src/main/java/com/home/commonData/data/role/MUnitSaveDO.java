package com.home.commonData.data.role;

import com.home.commonData.data.item.EquipContainerDO;
import com.home.shineData.support.MaybeNull;

/** 主单位存库数据 */
public class MUnitSaveDO
{
	/** 角色ID */
	int id;
	/** 缓存数据 */
	@MaybeNull
	MUnitCacheDO cache;
	/** 装备组 */
	EquipContainerDO equips;
	/** 主单位序号 */
	int mIndex;
}
