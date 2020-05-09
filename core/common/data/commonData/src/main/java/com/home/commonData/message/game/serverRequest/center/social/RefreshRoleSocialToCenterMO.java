package com.home.commonData.message.game.serverRequest.center.social;

import com.home.commonData.data.role.RoleShowChangeDO;
import com.home.commonData.message.game.serverRequest.center.base.PlayerToCenterMO;

/** 更新角色显示数据到中心服 */
public class RefreshRoleSocialToCenterMO extends PlayerToCenterMO
{
	/** 改变数据 */
	RoleShowChangeDO data;
}
