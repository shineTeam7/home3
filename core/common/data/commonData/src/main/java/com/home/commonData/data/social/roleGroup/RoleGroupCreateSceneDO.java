package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.scene.scene.CreateSceneDO;

/** 玩家群创建场景数据 */
public class RoleGroupCreateSceneDO extends CreateSceneDO
{
	/** 功能id */
	int funcID;
	/** 玩家群id */
	long groupID;
	/** 配置id */
	int configID;
	/** 等级 */
	int level;
	/** 玩家群名 */
	String name;
}
