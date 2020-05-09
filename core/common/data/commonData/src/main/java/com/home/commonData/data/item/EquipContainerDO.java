package com.home.commonData.data.item;

import com.home.commonData.data.func.FuncToolDO;

import java.util.Map;
import java.util.Set;

/** 装备容器数据 */
public class EquipContainerDO extends FuncToolDO
{
	/** 装备数据组(key:slot) */
	Map<Integer,ItemDO> equips;
	/** 开启的格子组 */
	Set<Integer> openSlots;
}
