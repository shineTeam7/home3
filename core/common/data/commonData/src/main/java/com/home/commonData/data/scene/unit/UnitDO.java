package com.home.commonData.data.scene.unit;

import com.home.shineData.support.MaybeNull;

/** 单位数据 */
public class UnitDO
{
	/** 流水ID */
	int instanceID;
	
	/** 身份数据 */
	UnitIdentityDO identity;
	
	/** 通用数据 */
	UnitNormalDO normal;
	
	/** 位置数据 */
	@MaybeNull
	UnitPosDO pos;
	
	/** 造型数据 */
	@MaybeNull
	UnitAvatarDO avatar;
	
	/** 移动数据 */
	@MaybeNull
	UnitMoveDO move;
	
	/** 战斗数据 */
	@MaybeNull
	UnitFightDO fight;
	
	/** 附加战斗数据 */
	@MaybeNull
	UnitFightExDO fightEx;
	
	/** AI数据 */
	@MaybeNull
	UnitAIDO ai;
	
	/** 功能数据 */
	@MaybeNull
	UnitFuncDO func;
}
