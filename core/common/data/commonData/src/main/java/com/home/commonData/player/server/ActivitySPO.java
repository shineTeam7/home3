package com.home.commonData.player.server;

import com.home.commonData.data.activity.ActivityDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;
import java.util.Set;

/** 活动数据 */
public class ActivitySPO
{
	/** 数据组 */
	@MapKeyInValue("id")
	Map<Integer,ActivityDO> datas;
	/** 使用过的激活码 */
	Set<String> usedActivationCodes;
}
