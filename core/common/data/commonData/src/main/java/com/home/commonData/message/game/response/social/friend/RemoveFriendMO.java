package com.home.commonData.message.game.response.social.friend;

import com.home.shineData.support.NeedFunctionOpen;
import com.home.shineData.support.ResponseBind;

/** 移除好友消息 */
@ResponseBind({})
@NeedFunctionOpen("Friend")
public class RemoveFriendMO
{
	/** 角色ID */
	long playerID;
}
