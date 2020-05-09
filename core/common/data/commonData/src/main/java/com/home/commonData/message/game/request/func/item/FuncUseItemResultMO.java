package com.home.commonData.message.game.request.func.item;

import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 使用物品结果消息 */
public class FuncUseItemResultMO extends FuncSMO
{
	/** 道具ID(使用的) */
	int itemID;
	/** 数目 */
	int num;
	/** 结果 */
	boolean result;
}
