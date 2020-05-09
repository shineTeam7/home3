package com.home.commonData.gameGlobal.server;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 社交数据 */
public class GameSocialSPO
{
	/** 角色社交数据字典(已废弃) */
	@MapKeyInValue("showData.playerID")
	@Deprecated
	Map<Long,RoleSocialDO> roleSocialDataDic;
}
