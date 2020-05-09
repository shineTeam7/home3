using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public abstract class BaseVersionControl<V>
{
	private IntObjectMap<Action<V>> _cDic=new IntObjectMap<Action<V>>();
	private IntObjectMap<Action<V>> _gDic=new IntObjectMap<Action<V>>();

	protected void registCFunc(int version,Action<V> func)
	{
		_cDic.put(version,func);
	}

	protected void registGFunc(int version,Action<V> func)
	{
		_gDic.put(version,func);
	}

	protected abstract SaveVersionData getVersionData(V me);

	/** 检查版本 */
	public void checkVersion(V me)
	{
		SaveVersionData vData=getVersionData(me);
		Action<V> func;

		if(vData.cVersion!=CommonSetting.cVersion)
		{
			if(CommonSetting.cVersion<vData.cVersion)
			{
				Ctrl.throwError("版本号不能更低C");
				ShineSetup.exit();
				return;
			}

			for(int i=vData.cVersion+1;i<=CommonSetting.cVersion;++i)
			{
				if((func=_cDic.get(i))!=null)
				{
					func(me);
				}

				vData.cVersion=i;
			}
		}

		if(vData.gVersion!=CommonSetting.gVersion)
		{
			if(CommonSetting.gVersion<vData.gVersion)
			{
				Ctrl.throwError("版本号不能更低G");
				ShineSetup.exit();
				return;
			}

			for(int i=vData.gVersion+1;i<=CommonSetting.gVersion;++i)
			{
				if((func=_gDic.get(i))!=null)
				{
					func(me);
				}

				vData.gVersion=i;
			}
		}
	}
}