package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.data.social.roleGroup.PlayerRoleGroupMemberDO;
import com.home.commonData.message.game.request.func.base.FuncPlayerRoleGroupSMO;

/** 推送添加成员消息 */
public class FuncSendRoleGroupAddMemberMO extends FuncPlayerRoleGroupSMO
{
	PlayerRoleGroupMemberDO data;
	/** 方式 */
	int type;
}
