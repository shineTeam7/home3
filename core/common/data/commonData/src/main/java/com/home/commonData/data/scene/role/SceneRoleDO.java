package com.home.commonData.data.scene.role;

import com.home.commonData.data.role.RoleShowDO;
import com.home.shineData.support.MaybeNull;

/** 场景角色数据 */
public class SceneRoleDO
{
	/** 角色ID */
	long playerID;
	/** 角色显示数据(中立角色为空) */
	@MaybeNull
	RoleShowDO showData;
	/** 属性数据 */
	@MaybeNull
	RoleAttributeDO attribute;
	/** 势力数据 */
	RoleForceDO force;
}
