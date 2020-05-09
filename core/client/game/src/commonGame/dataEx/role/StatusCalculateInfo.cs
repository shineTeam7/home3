using System;
using ShineEngine;

/// <summary>
/// 状态统计信息
/// </summary>
public class StatusCalculateInfo
{
	public int size;

	public int[] allList;

	/** 初始化 */
	public void init(SList<StatusOneInfo> list,int size)
	{
		this.size=size;

		IntList tAllList=new IntList();

		StatusOneInfo[] values=list.getValues();
		StatusOneInfo v;

		for(int i=0,len=list.size();i<len;++i)
		{
			v=values[i];

			int type=v.id;

			tAllList.add(type);
		}

		allList=tAllList.toArray();
	}
}