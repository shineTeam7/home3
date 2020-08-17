package com.home.commonBase.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.utils.FileUtils;
import com.home.shine.utils.StringUtils;

import java.io.IOException;

/** Created by 123 on 2020/4/8. **/
public class FirebasePushNotifyUtils extends PushNotifyBaseUtils
{
	private boolean inited;
	
	public FirebasePushNotifyUtils()
	{
		try
		{
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(FileUtils.readFileForInputStream(ShineGlobal.serverConfigRoot+"/firebase.json")))
					.setDatabaseUrl(CommonSetting.firebaseDatabaseUrl)
					.build();
			FirebaseApp.initializeApp(options);
			inited=true;
		}
		catch(IOException e)
		{
			Ctrl.errorLog("firebase key 路径错误");
		}
	}
	
	@Override
	public void pushSingle(String account,String title,String content)
	{
		if(!inited)
		{
			Ctrl.errorLog("FirebasePushNotifyUtils not init");
			return;
		}
		Ctrl.log("pushSingle","开始推送",account,title,content);
		// This registration token comes from the client FCM SDKs.
		String registrationToken = account;
		String titleNew= StringUtils.replaceUnicodePlusToString(title);
		String contentNew=StringUtils.replaceUnicodePlusToString(content);
		// See documentation on defining a message payload.
		Message message = Message.builder()
				.putData("title",title)
				.setToken(registrationToken)
				.setNotification(new Notification(titleNew,contentNew))
				.build();
		// Send a message to the device corresponding to the provided
		// registration token.
		String response =null;
		try
		{
			response=FirebaseMessaging.getInstance().send(message);
		}
		catch(FirebaseMessagingException e)
		{
			e.printStackTrace();
		}
		// Response is a message ID string.
		Ctrl.log("pushSingle Successfully sent message: ",response);
	}
	
	@Override
	public void pushAll(String title,String content,String topic)
	{
		if(!inited)
		{
			Ctrl.errorLog("FirebasePushNotifyUtils not init");
			return;
		}
		String titleNew= StringUtils.replaceUnicodePlusToString(title);
		String contentNew=StringUtils.replaceUnicodePlusToString(content);
		boolean isCondition=topic.contains("||")||topic.contains("&&");
		Message.Builder builder= Message.builder()
				.putData("title",title)
				.setNotification(new Notification(titleNew,contentNew));
		if(isCondition)
		{
			Ctrl.log("pushAll","isCondition",topic,title,content);
			builder.setCondition(topic);
		}
		else
		{
			Ctrl.log("pushAll","topic",topic,title,content);
			builder.setTopic(topic);
		}
		// Send a message to the devices subscribed to the provided topic.
		String response =null;
		Message message=builder.build();
		try
		{
			response=FirebaseMessaging.getInstance().send(message);
		}
		catch(FirebaseMessagingException e)
		{
			e.printStackTrace();
		}
		// Response is a message ID string.
		Ctrl.log("pushAll Successfully sent message: ",response);
	}
	
	/** 获得当前环境参数 */
	private String getEnv()
	{
		return CommonSetting.pushNotifyDevelopEnvironment? "test":"pro";
	}
}
