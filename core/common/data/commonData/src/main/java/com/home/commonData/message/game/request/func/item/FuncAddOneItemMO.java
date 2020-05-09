package com.home.commonData.message.game.request.func.item;

import com.home.commonData.data.item.ItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 推送添加单个物品 */
public class FuncAddOneItemMO extends FuncSMO
{
	/** 途径 */
	int way;
	/** 序号 */
	int index;
	/** 物品数据 */
	ItemDO item;
}
