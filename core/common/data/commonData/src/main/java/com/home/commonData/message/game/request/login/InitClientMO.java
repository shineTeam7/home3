package com.home.commonData.message.game.request.login;

import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MaybeNull;
import com.home.shineData.support.MessageLong;

/** 初始化客户端消息(ClientListData)(此处需要使用copy,因工具已改为潜拷,为了逻辑层避开深拷问题) */
@MessageLong
public class InitClientMO
{
	/** 客户端列表数据 */
	BaseDO listData;
	/** 配置热更数据 */
	@MaybeNull
	byte[] configHotfix;
}
