package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 推送玩家群移除成员 */
public class FuncSendRoleGroupRemoveMemberMO extends FuncPlayerRoleGroupSMO
{
	long playerID;
	/** 方式 */
	int type;
}
