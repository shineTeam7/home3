package com.home.commonData.player.server;

import com.home.commonData.data.mail.MailDO;

import java.util.List;

/** 邮件数据 */
public class MailSPO
{
	/** 邮件组 */
	List<MailDO> mails;
	/** 上次游戏服邮件序号 */
	int lastGameMailIndex;
}
