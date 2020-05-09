package com.home.commonData.data.social;

import com.home.commonData.data.func.FuncToolDO;

import java.util.Map;

/** 角色社交数据池工具数据 */
public class RoleSocialPoolToolDO extends FuncToolDO
{
	/** 数据 */
	Map<Long,RoleSocialPoolDO> dic;
	/** 下次裁剪 */
	long nextCutTime;
}
