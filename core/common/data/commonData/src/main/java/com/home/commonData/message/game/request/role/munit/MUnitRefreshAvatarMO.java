package com.home.commonData.message.game.request.role.munit;

import java.util.Map;

/** 控制单位刷新造型消息 */
public class MUnitRefreshAvatarMO extends MUnitSMO
{
	/** 模型ID */
	int modelID;
	/** 改变组 */
	Map<Integer,Integer> parts;
}
