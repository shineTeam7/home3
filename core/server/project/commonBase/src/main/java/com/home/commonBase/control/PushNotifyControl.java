package com.home.commonBase.control;

import com.home.commonBase.config.game.PushNotifyConfig;
import com.home.commonBase.config.game.enumT.PushTopicTypeConfig;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.utils.PushNotifyBaseUtils;
import com.home.shine.ShineSetup;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;

/** 推送控制器 */
public class PushNotifyControl
{
	/** 推送体 */
	private PushNotifyBaseUtils _push;
	
	public void init()
	{
		_push = PushNotifyBaseUtils.create(CommonSetting.pushNotifyPlatformType);
	}
	
	/** 推送单账号 */
	public void pushSingle(String account,int pushID)
	{
		if(!checkCanPush())
			return;
		
		PushNotifyConfig config = PushNotifyConfig.get(pushID);
		
		if(config==null)
			return;
		
		_push.pushSingle(account,config.title,config.text);
		
		Ctrl.log("pushSingle account by config",account,pushID);
	}
	
	/** 推送单账号 */
	public void pushSingle(String account,String title,String content)
	{
		if(!checkCanPush())
			return;
		
		_push.pushSingle(account,title,content);
		
		Ctrl.log("pushSingle account",account,title,content);
	}
	
	/** 推送所有 */
	public void pushAll(int pushID)
	{
		if(!checkCanPush())
			return;
		
		PushNotifyConfig config = PushNotifyConfig.get(pushID);
		
		if(config==null)
		{
			Ctrl.log("推送ID， pushID=",pushID,"配置文件未找到");
			return;
		}
		//标签
		PushTopicTypeConfig topicTypeConfig=PushTopicTypeConfig.get(config.topicType);
		if(topicTypeConfig==null)
		{
			Ctrl.errorLog("批量推送时,标签类型不正确",pushID);
			return;
		}
		_push.pushAll(config.title,config.text,topicTypeConfig.topic);
		
		Ctrl.log("pushAll all by config",pushID);
	}
	
	/** 推送所有 */
	public void pushAll(String topic,String title,String content)
	{
		if(!checkCanPush())
			return;
		
		_push.pushAll(title,content,topic);
		
		Ctrl.log("pushSingle all",title,content);
	}
	
	/** 检测是否可以发送推送 */
	private boolean checkCanPush()
	{
		if(!CommonSetting.openPushNotification)
		{
			Ctrl.log("检查push配置，openPushNotification =false，不能推送");
			return false;
		}

		
		if(_push==null)
		{
			Ctrl.log("检查push配置，_push=null，不能推送");
			return false;
		}
		
		if(!ShineSetting.isRelease)
		{
			Ctrl.log("测试环境，不能推送");
			return false;
		}
		
		return true;
	}
}
