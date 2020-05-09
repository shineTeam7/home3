package com.home.commonData.message.game.serverRequest.game.system;

import com.home.shineData.support.MessageLong;

import java.util.Map;

/** 保存切换后的角色数据消息 */
@MessageLong
public class SaveSwitchedPlayerListMO
{
	/** 数据组(playerID->listData(序列化好的)) */
	Map<Long,byte[]> datas;
}
