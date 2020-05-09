package com.home.commonData.message.game.serverRequest.game.func.roleGroup;


import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameMO;

/** 更新职位到角色 */
public class FuncRefreshTitleRoleGroupToPlayerMO extends FuncPlayerRoleGroupGameToGameMO
{
	/** 成员id */
	long memberID;
	/** 职位 */
	int title;
}
