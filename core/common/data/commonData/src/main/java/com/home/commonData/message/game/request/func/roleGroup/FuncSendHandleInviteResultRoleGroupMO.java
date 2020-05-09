package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.data.role.RoleShowDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 推送处理邀请结果到邀请者 */
public class FuncSendHandleInviteResultRoleGroupMO extends FuncSMO
{
	/** 被邀请者 */
	RoleShowDO showData;
	
	int result;
}
