package com.home.commonData.data.scene.unit;

/** 单位身份数据 */
public class UnitIdentityDO
{
	/** 类型(见UnitType) */
	int type;
	/** 归属角色ID(角色的归属就是自身角色ID)(没有归属就是-1) */
	long playerID=-1L;
	/** 所属势力(playerID为-1时有意义) */
	int force=0;
}
