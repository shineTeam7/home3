package com.home.commonData.message.game.serverRequest.center.system;

import com.home.commonData.data.system.CenterGlobalWorkDO;
import com.home.shineData.support.MessageDontCopy;

/** 推送中心服事务到中心服 */
@MessageDontCopy
public class SendCenterWorkToCenterMO
{
	/** 事务 */
	CenterGlobalWorkDO data;
}
