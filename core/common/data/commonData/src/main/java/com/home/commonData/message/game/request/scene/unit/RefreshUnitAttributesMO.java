package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.message.game.request.scene.base.UnitSMO;
import com.home.shineData.support.MessageDontCopy;

import java.util.Map;

/** 刷新单位属性 */
@MessageDontCopy
public class RefreshUnitAttributesMO extends UnitSMO
{
	/** 改变的属性组 */
	Map<Integer,Integer> attributes;
}
