package com.home.commonData.data.scene.unit.identity;

import com.home.commonData.data.role.RoleShowDO;

/** 角色身份数据基类 */
public class CharacterIdentityDO extends MUnitIdentityDO
{
	/** 序号(帧同步使用) */
	int syncIndex;
	/** 玩家显示数据 */
	RoleShowDO roleShowData;
}
