using System;
using ShineEngine;

/// <summary>
/// tigger配置
/// </summary>
public class TriggerConfig
{
	/** 集合 */
	private static IntObjectMap<TriggerConfigData> _dic;

	private static IntObjectMap<IntObjectMap<IntObjectMap<TriggerConfigData>>> _groupDic=new IntObjectMap<IntObjectMap<IntObjectMap<TriggerConfigData>>>();

	/** 设置字典 */
	public static void setDic(IntObjectMap<TriggerConfigData> dic)
	{
		_dic=dic;
	}

	/** 读完所有表后处理 */
	public static void afterReadConfigAll()
	{
		_groupDic.clear();

		TriggerConfigData[] values;
		TriggerConfigData v;

		for(int i=(values=_dic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				_groupDic.computeIfAbsent(v.groupType,k1=>new IntObjectMap<IntObjectMap<TriggerConfigData>>())
					.computeIfAbsent(v.groupID,k2=>new IntObjectMap<TriggerConfigData>())
					.put(v.id,v);
			}
		}
	}

	/** 获取某组trigger */
	public static IntObjectMap<TriggerConfigData> getGroupDic(int groupType,int groupID)
	{
		IntObjectMap<IntObjectMap<TriggerConfigData>> dic;

		if((dic=_groupDic.get(groupType))==null)
			return null;

		return dic.get(groupID);
	}
}