package com.home.commonData.message.manager.serverRequest.game;

import com.home.commonData.data.login.ClientVersionDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 游戏服热更消息 */
public class HotfixToGameMO
{
	/** 客户端版本 */
	@MapKeyInValue("type")
	Map<Integer,ClientVersionDO> clientVersion;
}
