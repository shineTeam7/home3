package com.home.commonData.data.item;

import com.home.shineData.support.MaybeNull;

/** 物品数据 */
public class ItemDO
{
	/** 物品ID */
	int id;
	/** 物品数目 */
	int num;
	/** 是否绑定 */
	boolean isBind;
	/** 失效时间(-1为失效或过期) */
	long disableTime=-1L;
	/** 身份数据 */
	@MaybeNull
	ItemIdentityDO identity;
	/** 是否有红点 */
	boolean hasRedPoint;
}
