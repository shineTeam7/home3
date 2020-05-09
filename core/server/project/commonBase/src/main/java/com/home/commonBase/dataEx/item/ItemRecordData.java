package com.home.commonBase.dataEx.item;

import com.home.shine.support.pool.PoolObject;

/** 道具字典记录数据 */
public class ItemRecordData extends PoolObject
{
	/** 物品ID */
	public int id;
	/** 总数 */
	public int num=0;
	/** 添加序号(第一个不满的该物品位置索引,如全满也为-1) */
	public int addIndex=-1;
	/** 删除序号(最后一个改物品的位置，只有改物品数目为0，才为-1) */
	public int removeIndex=-1;
	
	@Override
	public void clear()
	{
		num=0;
		addIndex=-1;
		removeIndex=-1;
	}
}
