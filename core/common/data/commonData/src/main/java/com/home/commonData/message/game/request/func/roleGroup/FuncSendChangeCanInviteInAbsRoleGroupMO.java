package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 发送被邀请时是否可直接入群的变化 */
public class FuncSendChangeCanInviteInAbsRoleGroupMO extends FuncSMO
{
	/** 被邀请时是否可直接入群(无需同意) */
	boolean canInviteInAbs;
}
