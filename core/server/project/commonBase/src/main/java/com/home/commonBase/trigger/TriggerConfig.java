package com.home.commonBase.trigger;

import com.home.shine.data.trigger.TriggerConfigData;
import com.home.shine.support.collection.IntObjectMap;

/** trigger配置 */
public class TriggerConfig
{
	/** 集合 */
	private static IntObjectMap<TriggerConfigData> _dic;
	
	private static IntObjectMap<IntObjectMap<IntObjectMap<TriggerConfigData>>> _groupDic=new IntObjectMap<>();
	
	/** 设置字典 */
	public static void setDic(IntObjectMap<TriggerConfigData> dic)
	{
		_dic=dic;
	}
	
	/** 读完所有表后处理 */
	public static void afterReadConfigAll()
	{
		_groupDic.clear();
		
		_dic.forEachValue(v->
		{
			_groupDic.computeIfAbsent(v.groupType,k1->new IntObjectMap<>())
					.computeIfAbsent(v.groupID,k2->new IntObjectMap<>(TriggerConfigData[]::new))
					.put(v.id,v);
		});
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
