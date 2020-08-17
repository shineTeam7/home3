package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.message.sceneBase.request.base.UnitSMO;
import com.home.shineData.support.MessageDontCopy;

import java.util.Map;

/** 刷新单位属性 */
@MessageDontCopy
public class RefreshUnitAttributesMO extends UnitSMO
{
	/** 改变的属性组 */
	Map<Integer,Integer> attributes;
}
