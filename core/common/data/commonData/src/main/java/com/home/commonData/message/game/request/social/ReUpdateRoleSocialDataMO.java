package com.home.commonData.message.game.request.social;

import com.home.commonData.data.social.RoleSocialDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

/** 回复更新社交数据消息 */
public class ReUpdateRoleSocialDataMO
{
	/** 变化的社交数据字典 */
	@MapKeyInValue("showData.playerID")
	Map<Long,RoleSocialDO> dic;
}
