package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 群里有权限成员通知处理申请结果 */
public class FuncSendHandleApplyResultToMemberMO extends FuncPlayerRoleGroupSMO
{
	/** 目标id */
	long targetID;
	/** 结果 */
	int result;
}
