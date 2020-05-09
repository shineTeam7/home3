package com.home.commonBase.config.other;

import com.home.commonBase.config.game.ScenePlaceElementConfig;
import com.home.commonBase.global.BaseC;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.support.collection.IntObjectMap;

/** 场景摆放配置 */
public class ScenePlaceConfig
{
	/** 数据组 */
	private static IntObjectMap<ScenePlaceConfig> _dic=new IntObjectMap<>(ScenePlaceConfig[]::new);
	
	/** 场景ID */
	public int id;
	/** 元素组 */
	public IntObjectMap<ScenePlaceElementConfig> elements=new IntObjectMap<>(ScenePlaceElementConfig[]::new);
	
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
		
		for(int i=(values=dic.getValues()).length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				addSceneConfig(v);
			}
		}
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
			
			for(int i2=(values2=config.elements.getValues()).length-1;i2>=0;--i2)
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
		return _dic.computeIfAbsent(id,k->new ScenePlaceConfig(k));
	}
	
	/** 添加元素 */
	public void addElement(ScenePlaceElementConfig element)
	{
		if(BaseC.config.isIniting() && ShineSetting.openCheck)
		{
			if(elements.contains(element.instanceID))
			{
				Ctrl.throwError("场景元素重复,sceneID:",element.sceneID,"instanceID:",element.instanceID);
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
