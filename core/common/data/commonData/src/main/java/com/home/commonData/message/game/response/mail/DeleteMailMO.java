package com.home.commonData.message.game.response.mail;

import com.home.shineData.support.ResponseBind;

/** 删除邮件消息 */
@ResponseBind({})
public class DeleteMailMO
{
	/** 邮件序号 */
	int index;
	/** 邮件实例ID */
	int instanceID;
}
