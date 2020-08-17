package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.message.sceneBase.request.base.UnitSMO;

import java.util.Map;

/** 刷新单位显示部件数据 */
public class RefreshUnitAvatarPartMO extends UnitSMO
{
	/** 改变组 */
	Map<Integer,Integer> parts;
}
