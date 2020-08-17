package com.home.commonData.message.manager.serverRequest.login;

import com.home.commonData.data.login.ClientVersionDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 登录服热更新消息 */
public class HotfixToLoginMO
{
	/** 客户端版本 */
	@MapKeyInValue("type")
	Map<Integer,ClientVersionDO> clientVersion;
	/** url重定向组 */
	Map<Integer,Map<Integer,String>> redirectURLDic;
}
