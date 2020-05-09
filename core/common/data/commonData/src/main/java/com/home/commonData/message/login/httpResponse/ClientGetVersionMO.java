package com.home.commonData.message.login.httpResponse;

/** 客户端获取版本消息 */
public class ClientGetVersionMO
{
	/** 客户端平台类型(见ClientPlatformType) */
	int platformType;
	/** 当前资源版本号(用来做url重定向) */
	int resourceVersion;
}
