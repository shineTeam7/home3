package com.home.commonData.data.item;

import com.home.commonData.data.func.FuncToolDO;

import java.util.Map;

/** 物品列表容器工具数据 */
public class ItemDicContainerDO extends FuncToolDO
{
	/** 物品数据组(key:index) */
	Map<Integer,ItemDO> items;
	/** 服务器序号自增 */
	int serverItemIndex;
	/** 客户端序号自增 */
	int clientItemIndex;
}
