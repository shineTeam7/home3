package com.home.commonClient.control;

import com.home.commonBase.config.other.MapInfoConfig;
import com.home.commonBase.extern.ExternMethodNative;
import com.home.commonBase.global.CommonSetting;

/** 场景控制 */
public class SceneControl
{
	public void init()
	{
		if(CommonSetting.serverMapNeedRecast)
		{
			MapInfoConfig.getDic().forEachValue(v->
			{
				if(v.recast.bytes!=null && v.recast.bytes.length>0)
				{
					//注册地图
					ExternMethodNative.registMap(v.id,v.recast.bytes);
				}
			});
		}
	}
}
