package com.home.commonData.player.server;

import com.home.commonData.data.social.chat.ChatChannelDO;

import java.util.Map;

/** 社交数据 */
public class SocialSPO
{
	/** 单一频道组 */
	Map<Integer,ChatChannelDO> singleChannels;
	/** 多频道组 */
	Map<Integer,Map<Long,ChatChannelDO>> multiChannels;
	/** 聊天发送序号 */
	int chatSendIndex;
}
