package com.home.commonData.player.client;

import com.home.commonData.data.social.friend.ApplyAddFriendDO;
import com.home.commonData.data.social.friend.ContactDO;
import com.home.commonData.data.social.friend.FriendDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

public class FriendCPO
{
	/** 好友组(存库为了离线) */
	@MapKeyInValue("playerID")
	Map<Long,FriendDO> friends;
	/** 黑名单组 */
	@MapKeyInValue("playerID")
	Map<Long,ContactDO> blackList;
	/** 申请添加好友字典 */
	@MapKeyInValue("playerID")
	Map<Long,ApplyAddFriendDO> applyDic;
}
