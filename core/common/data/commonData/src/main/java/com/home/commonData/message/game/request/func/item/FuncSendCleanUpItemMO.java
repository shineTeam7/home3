package com.home.commonData.message.game.request.func.item;

import com.home.commonData.data.item.ItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 推送整理物品消息 */
public class FuncSendCleanUpItemMO extends FuncSMO
{
	/** 道具组 */
	@MaybeNull
	List<ItemDO> items;
}
