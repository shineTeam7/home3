package com.home.commonData.data.login;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.commonData.data.social.roleGroup.PlayerRoleGroupDO;
import com.home.shineData.support.MapKeyInValue;
import com.home.shineData.support.OnlyS;

import java.util.Map;

@OnlyS
public class RePlayerLoginFromEachGameDO
{
	/** 社交数据组 */
	@MapKeyInValue("showData.playerID")
	Map<Long,RoleSocialDO> roleSocialDatas;
	/** 玩家群组(key:funcID) */
	Map<Integer,Map<Long,PlayerRoleGroupDO>> roleGroups;
}
