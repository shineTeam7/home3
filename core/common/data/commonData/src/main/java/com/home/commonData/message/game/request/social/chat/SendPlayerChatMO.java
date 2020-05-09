package com.home.commonData.message.game.request.social.chat;

import com.home.commonData.data.social.chat.RoleChatDO;

/** 推送角色聊天 */
public class SendPlayerChatMO
{
	/** 聊天数据 */
	RoleChatDO chatData;
	/** 频道 */
	int channel;
	/** 二级索引 */
	long key;
}
