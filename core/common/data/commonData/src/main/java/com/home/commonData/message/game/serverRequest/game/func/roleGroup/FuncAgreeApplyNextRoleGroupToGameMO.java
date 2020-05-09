package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 同意申请入群下一步 */
public class FuncAgreeApplyNextRoleGroupToGameMO extends FuncToGameMO
{
	/** 玩家群id */
	long groupID;
	/** 数据 */
	RoleSocialDO data;
}
