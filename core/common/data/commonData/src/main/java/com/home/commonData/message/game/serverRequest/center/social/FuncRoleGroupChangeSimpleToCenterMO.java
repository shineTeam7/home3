package com.home.commonData.message.game.serverRequest.center.social;

import com.home.commonData.data.social.roleGroup.RoleGroupChangeDO;
import com.home.commonData.message.game.serverRequest.center.base.FuncToCenterMO;

public class FuncRoleGroupChangeSimpleToCenterMO extends FuncToCenterMO
{
	long groupID;
	/** 改变数据 */
	RoleGroupChangeDO changeData;
}
