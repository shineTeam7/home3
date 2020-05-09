package com.home.commonData.message.game.request.func.roleGroup;

import com.home.commonData.data.social.roleGroup.PlayerRoleGroupDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 推送添加玩家群消息 */
public class FuncSendPlayerJoinRoleGroupMO extends FuncSMO
{
	/** 玩家群数据 */
	PlayerRoleGroupDO data;
	/** 方式 */
	int type;
}
