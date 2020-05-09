package com.home.commonData.data.login;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.data.social.roleGroup.PlayerRoleGroupSaveDO;
import com.home.shineData.support.OnlyS;

import java.util.Map;
import java.util.Set;

/** 角色登录到其他逻辑服的数据 */
@OnlyS
public class PlayerLoginToEachGameDO
{
	/** 自身社交数据 */
	RoleSocialDO selfData;
	/** 需要的角色社交数据组(玩家群单独算) */
	Set<Long> needRoleSocials;
	/** 玩家群组(key:funcID) */
	Map<Integer,Map<Long,PlayerRoleGroupSaveDO>> roleGroups;
}
