package com.home.commonData.data.social.chat;

import com.home.commonData.data.system.PlayerWorkDO;

/** 推送玩家聊天 */
public class SendPlayerChatOWDO extends PlayerWorkDO
{
	/** 聊天数据 */
	RoleChatDO chatData;
	/** 频道 */
	int channel;
	/** 二级索引 */
	long key;
}
