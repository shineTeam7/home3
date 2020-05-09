package com.home.commonData.message.game.response.social.friend;

import com.home.shineData.support.NeedFunctionOpen;
import com.home.shineData.support.ResponseBind;

/** 同意添加好友消息 */
@ResponseBind({})
@NeedFunctionOpen("Friend")
public class AgreeAddFriendMO
{
	/** 同意添加好友消息 */
	long playerID;
}
