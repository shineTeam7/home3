package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 推送玩家离开玩家群消息 */
public class FuncSendPlayerLeaveRoleGroupMO extends FuncSMO
{
	long groupID;
	/** 方式 */
	int type;
}
