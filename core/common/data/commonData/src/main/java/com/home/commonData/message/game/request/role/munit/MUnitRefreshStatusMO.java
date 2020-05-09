package com.home.commonData.message.game.request.role.munit;

import java.util.Map;

/** 刷新主角状态 */
public class MUnitRefreshStatusMO extends MUnitSMO
{
	/** 改变的状态组 */
	Map<Integer,Boolean> status;
}
