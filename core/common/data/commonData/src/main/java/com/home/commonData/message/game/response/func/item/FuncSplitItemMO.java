package com.home.commonData.message.game.response.func.item;

import com.home.commonData.message.game.response.func.base.FuncRMO;

/** 拆分物品(原物品至少保留1个) */
public class FuncSplitItemMO extends FuncRMO
{
	/** 物品格子 */
	int index;
	/** 拆出数目 */
	int num;
}
