package com.home.commonData.message.game.serverRequest.game.login;

import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MessageDontCopy;

/** 回复角色退出返回消息 */
@MessageDontCopy
public class PlayerExitSwitchBackMO
{
	/** 角色ID */
	long playerID;
	/** 角色列表数据 */
	BaseDO listData;
}
