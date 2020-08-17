package com.home.commonData.message.sceneBase.request.role;

import com.home.commonData.message.sceneBase.request.base.RoleSMO;

import java.util.Map;

/** 角色刷新属性 */
public class RoleRefreshAttributeMO extends RoleSMO
{
	/** 改变的属性组 */
	Map<Integer,Integer> attributes;
}
