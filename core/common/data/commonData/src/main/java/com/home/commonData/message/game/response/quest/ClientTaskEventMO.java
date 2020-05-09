package com.home.commonData.message.game.response.quest;

import com.home.shineData.support.MaybeNull;
import com.home.shineData.support.NeedFunctionOpen;

/** 客户端任务目标消息 */
@NeedFunctionOpen("Quest")
public class ClientTaskEventMO
{
	/** 类型 */
	int type;
	/** 参数组 */
	@MaybeNull
	int[] args;
}
