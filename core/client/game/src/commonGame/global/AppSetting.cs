using ShineEngine;
using UnityEngine;

/// <summary>
///
/// </summary>
public class AppSetting
{
	/** 初始化 */
	public static void init()
	{
		ShineGlobal.init();
		CommonSetting.init();

		if(ShineSetting.isRelease)
			initRelease();
		else
			initDebug();
	}

	/** 设置发布参数 */
	private static void initRelease()
	{
		ShineSetting.needPingCut=true;
		// ShineSetting.needError=false;
	}

	/** 设置调试参数 */
	private static void initDebug()
	{
		//cdn路径
		ShineGlobal.cdnSourcePath="file:///"+Application.dataPath + "/../../cdnSource";

		ShineSetting.needPingCut=false;

//		ShineSetting.useReflectDll=true;

		ShineSetting.resourceKeepCheckDelay=1;
		ShineSetting.resourceKeepTime=3;
		ShineSetting.uiDisposeKeepTime=3;

		//也关掉
		// ShineSetting.needError=false;
	}
}