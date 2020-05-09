package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 同意邀请创建到逻辑服 */
public class FuncAgreeInviteCreateRoleGroupToGameMO extends FuncToGameMO
{
	/** 邀请者数据 */
	RoleSocialDO invitor;
	/** 自身数据 */
	RoleSocialDO selfData;
}
