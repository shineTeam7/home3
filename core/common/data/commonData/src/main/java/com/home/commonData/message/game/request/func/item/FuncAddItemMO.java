package com.home.commonData.message.game.request.func.item;

import com.home.commonData.data.item.ItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;

/** 添加物品消息 */
public class FuncAddItemMO extends FuncSMO
{
	/** 途径 */
	int way;
	/** 自动使用物品组 */
	@MaybeNull
	Map<Integer,Integer> autoUseItems;
	/** 更新组 */
	@MaybeNull
	Map<Integer,ItemDO> items;
}
