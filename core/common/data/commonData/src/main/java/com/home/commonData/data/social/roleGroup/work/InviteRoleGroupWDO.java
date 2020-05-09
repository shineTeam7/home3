package com.home.commonData.data.social.roleGroup.work;

import com.home.commonData.data.func.PlayerFuncWorkDO;
import com.home.commonData.data.role.RoleShowDO;
import com.home.commonData.data.social.roleGroup.RoleGroupSimpleDO;
import com.home.shineData.support.MaybeNull;

/** 邀请进入玩家群消息 */
public class InviteRoleGroupWDO extends PlayerFuncWorkDO
{
	/** 邀请者 */
	RoleShowDO inviter;
	/** 玩家群简版数据 */
	@MaybeNull
	RoleGroupSimpleDO simpleData;
}
