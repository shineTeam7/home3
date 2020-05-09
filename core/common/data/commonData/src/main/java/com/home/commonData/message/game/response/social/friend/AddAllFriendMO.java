package com.home.commonData.message.game.response.social.friend;

import com.home.shineData.support.NeedFunctionOpen;
import com.home.shineData.support.ResponseBind;

import java.util.List;

/** 添加所有好友消息 */
@ResponseBind({})
@NeedFunctionOpen("Friend")
public class AddAllFriendMO
{
	/** 角色ID */
	List<Long> playerIDList;
	/** 添加方式(g层定义) */
	int type;
}
