package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.social.RoleSocialDO;

/** 玩家群成员数据 */
public class RoleGroupMemberDO
{
	/** 角色ID */
	long playerID;
	/** 社交数据 */
	RoleSocialDO socialData;
	/** 职位 */
	int title;
	/** 加入时间戳 */
	long joinTime;
}
