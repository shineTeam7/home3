package com.home.commonBase.utils;

import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.sdk.xinge.Message;
import com.home.commonBase.sdk.xinge.MessageIOS;
import com.home.commonBase.sdk.xinge.Style;
import com.home.commonBase.sdk.xinge.XingeApp;
import org.json.JSONObject;

/** 信鸽推送体 */
public class XinGePushNotifyUtils extends PushNotifyBaseUtils
{
	private XingeApp _xinge;
	private XingeApp _xingeAndroid;
	public XinGePushNotifyUtils()
	{
		_xinge=new XingeApp(CommonSetting.xingeAccessID,CommonSetting.xingeSecretKey);
		_xingeAndroid=new XingeApp(CommonSetting.xingeAndroidAccessID,CommonSetting.xingeAndroidSecretKey);
	}

	@Override
	public void pushSingle(String account,String title,String content)
	{
		_xinge.pushSingleAccount(0,account,buildIosMessage(title,content,""),getEnvironment());
		_xingeAndroid.pushSingleAccount(0,account,buildMessage(title,content,""));
	}
	
	@Override
	public void pushAll(String title,String content,String topic)
	{
		_xinge.pushAllDevice(0,buildIosMessage(title,content,""),getEnvironment());
		_xingeAndroid.pushAllDevice(0,buildMessage(title,content,""));
	}
	
	/** 获得当前环境参数 */
	private int getEnvironment()
	{
		return CommonSetting.pushNotifyDevelopEnvironment?XingeApp.IOSENV_DEV:XingeApp.IOSENV_PROD;
	}
	
	private Style buildStyle(String iconRes)
	{
		Style style=new Style(0,1,1,0,0,1,0,0);
		
		style.setIconRes(iconRes);
		
		return style;
	}
	
	private Message buildMessage(String title,String content,String iconRes)
	{
		Message message=new Message();
		message.setTitle(title);
		message.setContent(content);
		message.setType(Message.TYPE_NOTIFICATION);
		message.setStyle(buildStyle(iconRes));
		return message;
	}
	
	private MessageIOS buildIosMessage(String title,String content,String iconRes)
	{
		MessageIOS ios=new MessageIOS();
		JSONObject j = new JSONObject();
		j.put("title",title);
		j.put("body",content);
		ios.setAlert(j);
		ios.setBadge(1);
		ios.setSound("beep.wav");
		return ios;
	}
}
