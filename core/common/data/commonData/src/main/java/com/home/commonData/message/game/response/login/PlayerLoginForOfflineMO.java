package com.home.commonData.message.game.response.login;

import com.home.commonData.data.system.ClientOfflineWorkDO;
import com.home.shineData.support.MessageLong;

import java.util.List;

/** 离线角色登录消息 */
@MessageLong
public class PlayerLoginForOfflineMO extends PlayerLoginMO
{
	/** 离线记录组 */
	List<ClientOfflineWorkDO> records;
	/** 当前客户端种子序号 */
	int clientRandomSeedIndex;
}
