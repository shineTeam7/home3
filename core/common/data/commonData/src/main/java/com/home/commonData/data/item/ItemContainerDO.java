package com.home.commonData.data.item;

import com.home.commonData.data.func.FuncToolDO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 道具容器数据 */
public class ItemContainerDO extends FuncToolDO
{
	/** 道具组 */
	@MaybeNull
	List<ItemDO> items;
	/** 开启格子数(0为无限) */
	int gridNum;
	
}
