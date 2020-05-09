package com.home.commonData.message.game.serverRequest.login.system;

import com.home.commonData.data.system.AreaServerDO;
import com.home.shineData.support.MapKeyInValue;

import java.util.Map;

public class ReBeLoginToGameMO
{
	/** 承载区服列表 */
	@MapKeyInValue("areaID")
	Map<Integer,AreaServerDO> areas;
}
