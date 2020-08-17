using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
[Hotfix(needFactory = false)]
public class BaseConfig:BaseData
{
	protected override sealed void afterRead()
	{
		afterReadConfig();
	}

	/** 读完单个配置 */
	protected virtual void afterReadConfig()
	{

	}

	/** 更新内容 */
	public void refresh()
	{
		generateRefresh();
	}

	/** 生成的刷新 */
	protected virtual void generateRefresh()
	{

	}

	public static void toLoadSplit<T>(int type,string configName,int id,IntObjectMap<T> dic,Action<T> func) where T:BaseConfig
	{
		T config=dic.get(id);

		if(config!=null)
		{
			func(config);
			return;
		}

		BaseC.config.loadSplit(type,configName,id,v=>
		{
			T cConfig=(T)v;
			dic.put(id,cConfig);
			func(cConfig);
		});
	}

	public static T toGetSync<T>(int type,string configName,int id,IntObjectMap<T> dic) where T:BaseConfig
	{
		T config=dic.get(id);

		if(config!=null)
		{
			return config;
		}

		BaseConfig v=BaseC.config.getSplitConfigSync(type,configName,id);

		config=(T)v;
		dic.put(id,config);
		return config;
	}

	public static void toUnloadSplit(int type,string configName,int id)
	{
		BaseC.config.unloadSplit(configName,id);
	}
}