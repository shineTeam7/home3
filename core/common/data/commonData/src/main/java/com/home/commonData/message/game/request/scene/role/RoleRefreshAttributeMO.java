package com.home.commonData.message.game.request.scene.role;

import com.home.commonData.message.game.request.scene.base.RoleSMO;

import java.util.Map;

/** 角色刷新属性 */
public class RoleRefreshAttributeMO extends RoleSMO
{
	/** 改变的属性组 */
	Map<Integer,Integer> attributes;
}
