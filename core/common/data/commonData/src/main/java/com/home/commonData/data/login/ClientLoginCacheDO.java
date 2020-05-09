package com.home.commonData.data.login;

import com.home.shineData.support.OnlyC;

/** 客户端登录缓存数据 */
@OnlyC
public final class ClientLoginCacheDO
{
	/** uid */
	String uid;
	/** 平台类型 */
	String platform;
	/** 区服ID */
	int areaID=-1;
	/** 上次登录角色ID */
	long lastPlayerID;
	/** 服务器生成码 */
	int serverBornCode;
}
