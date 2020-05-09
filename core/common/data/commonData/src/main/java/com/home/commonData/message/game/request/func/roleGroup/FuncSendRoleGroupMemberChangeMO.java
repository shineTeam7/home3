package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.data.social.roleGroup.RoleGroupMemberChangeDO;
import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 玩家群成员数据改变消息 */
public class FuncSendRoleGroupMemberChangeMO extends FuncPlayerRoleGroupSMO
{
	long memberID;
	
	RoleGroupMemberChangeDO data;
}
