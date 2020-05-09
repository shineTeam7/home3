package com.home.commonData.data.social.friend;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.data.system.PlayerWorkDO;

/** 添加好友(双向)立即事务 */
public class AddFriendForOvonicAWDO extends PlayerWorkDO
{
	/** 社交数据 */
	RoleSocialDO data;
	/** 添加类型 */
	int type;
}
