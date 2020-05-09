package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 更新成员职位 */
public class FuncRefeshTitleRoleGroupMO extends FuncPlayerRoleGroupSMO
{
	/** 成员id */
	long memberID;
	/** 职位 */
	int title;
}
