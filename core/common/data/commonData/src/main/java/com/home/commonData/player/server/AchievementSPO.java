package com.home.commonData.player.server;

import com.home.commonData.data.quest.AchievementCompleteDO;
import com.home.commonData.data.quest.AchievementSaveDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 成就数据 */
public class AchievementSPO
{
	/** 运行中数据组 */
	@MapKeyInValue("id")
	Map<Integer,AchievementSaveDO> runningDatas;
	/** 完成数据组 */
	@MapKeyInValue("id")
	Map<Integer,AchievementCompleteDO> completeDatas;
}
