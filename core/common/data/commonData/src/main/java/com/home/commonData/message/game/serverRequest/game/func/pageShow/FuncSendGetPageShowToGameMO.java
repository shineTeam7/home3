package com.home.commonData.message.game.serverRequest.game.func.pageShow;

import com.home.commonData.message.game.serverRequest.game.func.base.FuncToGameMO;

/** 发送获取翻页显示到逻辑服 */
public class FuncSendGetPageShowToGameMO extends FuncToGameMO
{
	long playerID;
	
	int page;
	
	int arg;
	/** 获取rankData的key */
	long key;
}
