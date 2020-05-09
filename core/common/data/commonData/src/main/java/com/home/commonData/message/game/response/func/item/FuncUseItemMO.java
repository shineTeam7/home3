package com.home.commonData.message.game.response.func.item;

import com.home.commonData.data.item.UseItemArgDO;
import com.home.commonData.message.game.response.func.base.FuncRMO;
import com.home.shineData.support.MaybeNull;

/** 使用物品消息 */
public class FuncUseItemMO extends FuncRMO
{
	/** 格子序号 */
	int index;
	/** 数目 */
	int num;
	/** 道具ID(检验用) */
	int itemID;
	/** 额外使用参数 */
	@MaybeNull
	UseItemArgDO arg;
}
