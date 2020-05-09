package com.home.commonData.message.login.httpResponseResult;

import com.home.commonData.data.login.ClientVersionDO;
import com.home.shineData.support.MaybeNull;

public class ClientGetVersionRO
{
	/** 版本数据(空为服务器并无此版本) */
	@MaybeNull
	ClientVersionDO version;
	/** 重定向的url */
	String redirectURL;
}
