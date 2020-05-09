package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 同意邀请入群到逻辑服 */
public class FuncAgreeInviteRoleGroupToGameMO extends FuncToGameMO
{
	long groupID;
	/** 邀请者 */
	long invitor;
	/** 数据 */
	RoleSocialDO data;
}
