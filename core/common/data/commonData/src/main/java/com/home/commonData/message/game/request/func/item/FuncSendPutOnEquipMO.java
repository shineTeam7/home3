package com.home.commonData.message.game.request.func.item;

import com.home.commonData.data.item.ItemDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;

/** 穿装备 */
public class FuncSendPutOnEquipMO extends FuncSMO
{
	/** 槽位 */
	int slot;
	/** 数据 */
	ItemDO data;
}
