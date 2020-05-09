package com.home.commonData.message.game.request.role.munit;

/** 主角刷buff */
public class MUnitRefreshBuffMO extends MUnitSMO
{
	/** buff流水ID */
	int instanceID;
	/** 剩余时间 */
	int lastTime;
	/** 剩余次数 */
	int lastNum;
}
