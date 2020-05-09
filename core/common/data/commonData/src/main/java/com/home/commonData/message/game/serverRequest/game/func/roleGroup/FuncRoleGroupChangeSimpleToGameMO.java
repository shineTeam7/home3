package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.social.roleGroup.RoleGroupChangeDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 玩家群简版数据变更到逻辑服 */
public class FuncRoleGroupChangeSimpleToGameMO extends FuncToGameMO
{
	long groupID;
	/** 改变数据 */
	RoleGroupChangeDO changeData;
}
