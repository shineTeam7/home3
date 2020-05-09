package com.home.commonData.message.game.response.social.friend;

import com.home.shineData.support.NeedFunctionOpen;
import com.home.shineData.support.ResponseBind;

/** 添加好友消息 */
@ResponseBind({})
@NeedFunctionOpen("Friend")
public class AddFriendMO
{
	/** 角色ID */
	long playerID;
	/** 添加方式(g层定义) */
	int type;
}
