package com.home.commonData.message.game.response.mail;

import com.home.commonData.data.mail.MailDO;

/** 客户端发送邮件消息 */
public class ClientSendMailMO
{
	/** 目标玩家ID */
	long playerID;
	/** 邮件数据 */
	MailDO mail;
}
