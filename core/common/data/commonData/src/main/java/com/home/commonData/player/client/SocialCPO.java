package com.home.commonData.player.client;

import com.home.commonData.data.social.chat.ChatChannelDO;

import java.util.Map;

/** 角色社交数据 */
public class SocialCPO
{
	/** 单一频道组 */
	Map<Integer,ChatChannelDO> singleChannels;
	/** 多频道组 */
	Map<Integer,Map<Long,ChatChannelDO>> multiChannels;
	/** 聊天发送序号 */
	int chatSendIndex;
}
