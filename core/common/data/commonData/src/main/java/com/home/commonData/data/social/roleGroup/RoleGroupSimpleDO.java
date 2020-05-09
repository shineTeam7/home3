package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.system.KeyDO;

/** 玩家群简版数据 */
public class RoleGroupSimpleDO extends KeyDO
{
	/** 群id */
	long groupID;
	/** 等级 */
	int level;
	/** 群名 */
	String name;
	/** 公告 */
	String notice;
	/** 成员数 */
	int memberNum;
}
