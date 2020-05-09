package com.home.commonData.message.game.response.mail;

import com.home.shineData.support.ResponseBind;

/** 领取邮件内容消息 */
@ResponseBind({})
public class TakeMailMO
{
	/** 邮件序号 */
	int index;
	/** 邮件实例ID */
	int instanceID;
}
