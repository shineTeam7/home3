package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncRoleGroupToGameMO;

/** 设置职位消息 */
public class FuncSetTitleRoleGroupToGameMO extends FuncRoleGroupToGameMO
{
	/** 操作者 */
	long operator;
	/** 角色id */
	long targetID;
	/** 职位 */
	int title;
}
