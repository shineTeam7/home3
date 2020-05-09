package com.home.commonData.player.client;

import com.home.commonData.data.quest.AchievementCompleteDO;
import com.home.commonData.data.quest.AchievementDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

public class AchievementCPO
{
	/** 运行中数据组 */
	@MapKeyInValue("id")
	Map<Integer,AchievementDO> runningDatas;
	/** 完成数据组 */
	@MapKeyInValue("id")
	Map<Integer,AchievementCompleteDO> completeDatas;
}
