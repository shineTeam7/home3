package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.message.game.request.scene.base.UnitSMO;

import java.util.Map;

/** 刷新单位造型消息 */
public class RefreshUnitAvatarMO extends UnitSMO
{
	/** 模型ID */
	int modelID;
	/** 改变组 */
	Map<Integer,Integer> parts;
}
