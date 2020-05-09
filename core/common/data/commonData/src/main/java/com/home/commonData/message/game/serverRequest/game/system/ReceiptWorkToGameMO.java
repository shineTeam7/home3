package com.home.commonData.message.game.serverRequest.game.system;

/** 事务回执到game服 */
public class ReceiptWorkToGameMO
{
	/** 发起者索引(type+id) */
	int senderIndex;
	/** 事务ID */
	long instanceID;
	/** 结果 */
	int result;
}
