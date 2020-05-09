package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 推送更换队长消息 */
public class FuncSendChangeLeaderRoleGroupMO extends FuncPlayerRoleGroupSMO
{
	/** 上次队长 */
	long lastLeader;
	/** 现在的队长 */
	long nowLeader;
}
