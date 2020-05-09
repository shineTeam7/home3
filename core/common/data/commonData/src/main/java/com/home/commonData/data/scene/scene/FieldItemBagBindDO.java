package com.home.commonData.data.scene.scene;

import com.home.commonData.data.item.ItemDO;

import java.util.List;

/** 掉落物品包绑定数据 */
public class FieldItemBagBindDO
{
	/** 归属单位实例id */
	int instanceID;
	/** 物品组 */
	List<ItemDO> items;
	/** 移除时间 */
	long removeTime;
}
