package com.home.commonData.centerGlobal.server;

import com.home.commonData.data.activity.ActivityServerDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 活动数据 */
public class CenterActivitySPO
{
	/** 数据组 */
	@MapKeyInValue("id")
	Map<Integer,ActivityServerDO> datas;
}
