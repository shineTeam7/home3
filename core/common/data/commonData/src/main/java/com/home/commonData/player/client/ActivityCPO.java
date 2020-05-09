package com.home.commonData.player.client;

import com.home.commonData.data.activity.ActivityDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

public class ActivityCPO
{
	/** 数据组 */
	@MapKeyInValue("id")
	Map<Integer,ActivityDO> datas;
}
