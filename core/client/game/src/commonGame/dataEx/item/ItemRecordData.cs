using System;
using ShineEngine;

/// <summary>
/// 物品记录数据
/// </summary>
public class ItemRecordData:PoolObject
{
	/** 物品ID */
	public int id;
	/** 总数 */
	public int num=0;
	/** 添加序号 */
	public int addIndex=-1;
	/** 删除序号 */
	public int removeIndex=-1;

	public override void clear()
	{
		// id=0;
		num=0;
		addIndex=-1;
		removeIndex=-1;
	}
}