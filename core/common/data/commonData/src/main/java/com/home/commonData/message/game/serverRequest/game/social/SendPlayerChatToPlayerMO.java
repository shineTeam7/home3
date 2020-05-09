package com.home.commonData.message.game.serverRequest.game.social;

import com.home.commonData.data.social.chat.RoleChatDO;
import com.home.commonData.message.game.serverRequest.game.base.PlayerGameToGameMO;

/** 推送玩家聊天消息 */
public class SendPlayerChatToPlayerMO extends PlayerGameToGameMO
{
	/** 聊天数据 */
	RoleChatDO chatData;
	/** 频道 */
	int channel;
	/** 第二索引 */
	long key;
}
