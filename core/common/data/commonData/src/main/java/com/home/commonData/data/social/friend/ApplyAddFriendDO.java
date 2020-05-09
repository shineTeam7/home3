package com.home.commonData.data.social.friend;

import com.home.commonData.data.social.RoleSocialDO;

/** 申请添加好友数据 */
public class ApplyAddFriendDO
{
	/** 角色ID */
	long playerID;
	/** 社交数据 */
	RoleSocialDO data;
	/** 申请时间 */
	long applyTime;
	/** 添加类型 */
	int type;
}
