package com.home.commonData.data.login;

/** 客户端登录数据(可以有继承) */
public class ClientLoginDO
{
	/** uid */
	String uid;
	/** 平台类型 */
	String platform;
	/** 国家ID */
	int countryID=-1;
	/** 客户端设备平台类型 */
	int clientPlatformType;
	
	/** 设备类型 */
	String deviceType;
	/** 设备唯一标识 */
	String deviceUniqueIdentifier;
	
	/** 携带的游客uid(用来做登陆时直接绑定游客) */
	String visitorUID;
	
	/** 当前App版本 */
	int appVersion;
	/** 当前资源版本 */
	int resourceVersion;
}
