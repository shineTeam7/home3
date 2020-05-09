package com.home.commonData.data.social.roleGroup;

import com.home.commonData.data.role.RoleShowDO;
import com.home.shineData.support.MaybeNull;

/** 邀请玩家群接收数据 */
public class InviteRoleGroupReceiveDO
{
	/** 邀请者 */
	RoleShowDO inviter;
	/** 玩家群简版数据 */
	@MaybeNull
	RoleGroupSimpleDO simpleData;
	/** 邀请时间 */
	long time;
}
