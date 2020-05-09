package com.home.commonData.data.social;

import com.home.commonData.data.system.PlayerWorkDO;
import com.home.shineData.support.MaybeNull;

/** 回复获取角色社交数据在线数据 */
public class ReGetRoleSocialDataWDO extends PlayerWorkDO
{
	/** 获取类型 */
	int type;
	/** 目标角色ID */
	long playerID;
	/** 数据 */
	@MaybeNull
	RoleSocialDO data;
	/** 参数 */
	int arg;
}
