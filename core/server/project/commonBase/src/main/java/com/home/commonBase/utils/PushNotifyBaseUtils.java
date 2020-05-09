package com.home.commonBase.utils;

import com.home.commonBase.constlist.generate.PushNotifyPlatformType;
import com.home.shine.ctrl.Ctrl;

/** 推送基类 */
public class PushNotifyBaseUtils
{
	/** 通过配置单账号推送 */
	public void pushSingle(String account,String title,String content)
	{
		Ctrl.errorLog("need override");
	}
	
	/** 通过配置，推送所有 */
	public void pushAll(String title,String content,String topic)
	{
		Ctrl.errorLog("need override");
	}
	
	/** 创建推送对象 */
	public static PushNotifyBaseUtils create(int type)
	{
		switch(type)
		{
			case PushNotifyPlatformType.Xinge:
			{
				return new XinGePushNotifyUtils();
			}
			case PushNotifyPlatformType.Firebase:
			{
				return new FirebasePushNotifyUtils();
			}
		}
		
		return null;
	}
}
