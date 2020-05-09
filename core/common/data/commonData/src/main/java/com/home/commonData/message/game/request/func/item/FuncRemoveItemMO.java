package com.home.commonData.message.game.request.func.item;

import com.home.commonData.message.game.request.func.base.FuncSMO;

import java.util.Map;

/** 移除物品消息 */
public class FuncRemoveItemMO extends FuncSMO
{
	/** 途径 */
	int way;
	/** 移除序号数目组 */
	Map<Integer,Integer> dic;
}
