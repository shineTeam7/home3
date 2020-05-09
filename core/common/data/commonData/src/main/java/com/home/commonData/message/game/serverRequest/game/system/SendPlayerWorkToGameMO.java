package com.home.commonData.message.game.serverRequest.game.system;

import com.home.commonData.data.system.PlayerWorkDO;
import com.home.shineData.support.MessageDontCopy;

/** 推送角色事务到逻辑服 */
@MessageDontCopy
public class SendPlayerWorkToGameMO
{
	/** 事务 */
	PlayerWorkDO data;
}
