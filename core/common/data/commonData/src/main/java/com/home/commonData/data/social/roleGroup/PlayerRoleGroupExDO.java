package com.home.commonData.data.social.roleGroup;

import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 玩家群补充数据(不实时推送的部分) */
public class PlayerRoleGroupExDO
{
	/** 群id */
	long groupID;
	/** 经验值 */
	long exp;
	/** 成员组 */
	@MapKeyInValue("playerID")
	Map<Long,PlayerRoleGroupMemberDO> members;
}
