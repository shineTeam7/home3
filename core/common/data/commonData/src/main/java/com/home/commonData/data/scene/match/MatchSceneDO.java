package com.home.commonData.data.scene.match;

import com.home.commonData.data.scene.scene.SceneLocationDO;
import com.home.shineData.support.MaybeNull;

/** 匹配后的场景信息 */
public class MatchSceneDO
{
	/** 功能id */
	int funcID;
	/** 匹配成功时刻 */
	long matchTime;
	/** 匹配后的场景数据 */
	SceneLocationDO location;
}
