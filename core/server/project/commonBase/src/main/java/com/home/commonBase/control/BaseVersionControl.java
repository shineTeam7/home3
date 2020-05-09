package com.home.commonBase.control;

import com.home.commonBase.data.system.SaveVersionData;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.ShineSetup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.func.ObjectCall;

public abstract class BaseVersionControl<V>
{
	private IntObjectMap<ObjectCall<V>> _cDic=new IntObjectMap<>();
	private IntObjectMap<ObjectCall<V>> _gDic=new IntObjectMap<>();
	
	protected void registCFunc(int version,ObjectCall<V> func)
	{
		_cDic.put(version,func);
	}
	
	protected void registGFunc(int version,ObjectCall<V> func)
	{
		_gDic.put(version,func);
	}
	
	protected abstract SaveVersionData getVersionData(V me);
	
	/** 检查版本 */
	public void checkVersion(V me)
	{
		SaveVersionData vData=getVersionData(me);
		ObjectCall<V> func;
		
		if(vData.cVersion!=CommonSetting.cVersion)
		{
			if(CommonSetting.cVersion<vData.cVersion)
			{
				ShineSetup.exit("版本号不能更低C");
				return;
			}
			
			for(int i=vData.cVersion+1;i<=CommonSetting.cVersion;++i)
			{
				if((func=_cDic.get(i))!=null)
				{
					func.apply(me);
				}
				
				vData.cVersion=i;
			}
		}
		
		if(vData.gVersion!=CommonSetting.gVersion)
		{
			if(CommonSetting.gVersion<vData.gVersion)
			{
				ShineSetup.exit("版本号不能更低G");
				return;
			}
			
			for(int i=vData.gVersion+1;i<=CommonSetting.gVersion;++i)
			{
				if((func=_gDic.get(i))!=null)
				{
					func.apply(me);
				}
				
				vData.gVersion=i;
			}
		}
	}
}
