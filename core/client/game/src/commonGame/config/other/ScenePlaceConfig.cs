using System;
using ShineEngine;

/// <summary>
/// 场景布置配置
/// </summary>
[Hotfix]
public class ScenePlaceConfig:BaseConfig
{
	/** 数据组 */
	private static IntObjectMap<ScenePlaceConfig> _dic=new IntObjectMap<ScenePlaceConfig>();

	private static IntSet _loadedSet=new IntSet();

	/** 场景ID */
	public int id;
	/** 元素组 */
	public IntObjectMap<ScenePlaceElementConfig> elements=new IntObjectMap<ScenePlaceElementConfig>();

	public ScenePlaceConfig()
	{

	}

	public ScenePlaceConfig(int id)
	{
		this.id=id;
	}

	/** 设置editor部分 */
	public static void setDic(IntObjectMap<ScenePlaceConfig> dic)
	{
		ScenePlaceConfig[] values;
		ScenePlaceConfig v;

		for(int i=(values=dic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				addSceneConfig(v);
			}
		}
	}

	public static void load(int id,Action<ScenePlaceConfig> func)
	{
		if(_loadedSet.contains(id))
		{
			func(get(id));
			return;
		}

		BaseC.config.loadSplit(ConfigType.ScenePlaceEditor,CommonSetting.scenePlaceEditor,id,v=>
		{
			ScenePlaceConfig cConfig=(ScenePlaceConfig)v;
			addSceneConfig(cConfig);

			func(get(id));
		});
	}

	public static ScenePlaceConfig getSync(int id)
	{
		if(_loadedSet.contains(id))
		{
			return get(id);
		}

		ScenePlaceConfig cConfig=(ScenePlaceConfig)BaseC.config.getSplitConfigSync(ConfigType.ScenePlaceEditor,CommonSetting.scenePlaceEditor,id);

		if(cConfig!=null)
		{
			addSceneConfig(cConfig);
		}

		return get(id);
	}

	public static void unload(int id)
	{
		BaseConfig.toUnloadSplit(ConfigType.ScenePlaceEditor,CommonSetting.scenePlaceEditor,id);
	}

	public static void addSceneConfig(ScenePlaceConfig config)
	{
		ScenePlaceConfig sConfig=_dic.get(config.id);

		if(sConfig==null)
		{
			_dic.put(config.id,config);
		}
		else
		{
			ScenePlaceElementConfig[] values2;
			ScenePlaceElementConfig v2;

			for(int i2=(values2=config.elements.getValues()).Length-1;i2>=0;--i2)
			{
				if((v2=values2[i2])!=null)
				{
					sConfig.addElement(v2);
				}
			}
		}
	}

	/** 获取场景布置配置 */
	public static ScenePlaceConfig get(int id)
	{
		return _dic.computeIfAbsent(id,k=>new ScenePlaceConfig(k));
	}

	/** 添加元素 */
	public void addElement(ScenePlaceElementConfig element)
	{
		if(BaseC.config.isIniting() && ShineSetting.openCheck)
		{
			if(elements.contains(element.instanceID))
			{
				Ctrl.errorLog("场景元素重复:",element.instanceID);
			}
		}

		elements.put(element.instanceID,element);
	}

	/** 获取单个摆放配置 */
	public ScenePlaceElementConfig getElement(int id)
	{
		return elements.get(id);
	}

	public static void afterReadConfigAll()
	{

	}
}