package com.home.commonData.message.game.response.social.friend;

import com.home.shineData.support.NeedFunctionOpen;
import com.home.shineData.support.ResponseBind;

/** 拒绝好友申请 */
@ResponseBind({})
@NeedFunctionOpen("Friend")
public class RefuseApplyAddFriendMO
{
	/** 角色ID */
	long playerID;
}
