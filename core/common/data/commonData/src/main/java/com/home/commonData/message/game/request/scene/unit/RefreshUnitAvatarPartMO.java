package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.message.game.request.scene.base.UnitSMO;

import java.util.Map;

/** 刷新单位显示部件数据 */
public class RefreshUnitAvatarPartMO extends UnitSMO
{
	/** 改变组 */
	Map<Integer,Integer> parts;
}
