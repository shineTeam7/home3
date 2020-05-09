package com.home.commonData.message.game.response.func.item;

import com.home.commonData.message.game.response.func.base.FuncRMO;

/** 脱装备(向背包) */
public class FuncPutOffEquipMO extends FuncRMO
{
	/** 装备槽位 */
	int slot;
	/** 背包序号(如传-1则为自动选择) */
	int bagIndex;
}
