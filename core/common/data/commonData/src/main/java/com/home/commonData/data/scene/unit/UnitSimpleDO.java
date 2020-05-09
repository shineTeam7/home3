package com.home.commonData.data.scene.unit;

import com.home.shineData.support.MaybeNull;

import java.util.Map;

/** 单位简版数据(只位置,血量,身份) */
public class UnitSimpleDO
{
	/** 流水ID */
	int instanceID;
	/** 身份数据 */
	UnitIdentityDO identity;
	/** 位置数据 */
	@MaybeNull
	UnitPosDO pos;
	/** 属性组(血量) */
	Map<Integer,Integer> attributes;
}
