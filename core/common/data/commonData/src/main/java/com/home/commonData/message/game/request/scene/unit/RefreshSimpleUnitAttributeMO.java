package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.message.game.request.scene.base.SceneSMO;

import java.util.Map;

/** 刷新简版单位属性消息 */
public class RefreshSimpleUnitAttributeMO extends SceneSMO
{
	int instanceID;
	
	/** 改变的属性组 */
	Map<Integer,Integer> attributes;
}
