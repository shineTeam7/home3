package com.home.commonData.gameGlobal.server;

import com.home.commonData.data.activity.ActivityServerDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 活动数据 */
public class GameActivitySPO
{
	/** 数据组 */
	@MapKeyInValue("id")
	Map<Integer,ActivityServerDO> datas;
}
