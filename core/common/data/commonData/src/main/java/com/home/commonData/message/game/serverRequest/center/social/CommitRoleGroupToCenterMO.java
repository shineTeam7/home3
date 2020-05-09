package com.home.commonData.message.game.serverRequest.center.social;

import com.home.commonData.data.social.roleGroup.RoleGroupSimpleDO;
import com.home.commonData.message.game.serverRequest.center.base.FuncToCenterMO;

/** 提交玩家群数据到中心服 */
public class CommitRoleGroupToCenterMO extends FuncToCenterMO
{
	/** 玩家群简版数据 */
	RoleGroupSimpleDO data;
}
