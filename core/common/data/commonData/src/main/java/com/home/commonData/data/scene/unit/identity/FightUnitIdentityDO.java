package com.home.commonData.data.scene.unit.identity;

import com.home.commonData.data.scene.unit.UnitIdentityDO;

/** 战斗单位身份数据 */
public class FightUnitIdentityDO extends UnitIdentityDO
{
	/** 单位类型ID(类型不同,数据表不同) */
	int id;
	/** 单位等级 */
	int level;
	/** 控制角色ID(服务器控制就是-1) */
	long controlPlayerID=-1L;
}
