package com.home.commonData.message.game.serverRequest.game.func.roleGroup;

import com.home.commonData.data.social.roleGroup.PlayerRoleGroupMemberDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerRoleGroupGameToGameMO;

/** 推送玩家群添加成员 */
public class FuncSendRoleGroupAddMemberToPlayerMO extends FuncPlayerRoleGroupGameToGameMO
{
	/** 成员数据 */
	PlayerRoleGroupMemberDO data;
	/** 方式 */
	int type;
}
