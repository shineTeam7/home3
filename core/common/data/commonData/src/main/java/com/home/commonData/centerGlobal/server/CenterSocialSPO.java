package com.home.commonData.centerGlobal.server;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;
import java.util.Set;

/** 中心服社交数据 */
public class CenterSocialSPO
{
	/** 角色社交数据字典 */
	@MapKeyInValue("showData.playerID")
	Map<Long,RoleSocialDO> roleSocialDataDic;
	/** 角色自身是否需要中心服社交数据记录(已废弃) */
	@Deprecated
	Set<Long> roleSelfNeedSocialSet;
}
