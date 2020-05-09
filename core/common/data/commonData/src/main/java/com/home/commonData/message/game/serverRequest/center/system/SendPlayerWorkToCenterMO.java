package com.home.commonData.message.game.serverRequest.center.system;

import com.home.commonData.data.system.PlayerWorkDO;
import com.home.shineData.support.MessageDontCopy;

/** 推送角色事务到中心服 */
@MessageDontCopy
public class SendPlayerWorkToCenterMO
{
	/** 事务 */
	PlayerWorkDO data;
}
