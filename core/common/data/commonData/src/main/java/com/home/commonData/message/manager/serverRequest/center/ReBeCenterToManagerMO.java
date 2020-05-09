package com.home.commonData.message.manager.serverRequest.center;

import com.home.commonData.data.system.ServerInfoDO;
import com.home.shineData.support.MaybeNull;

import java.util.Map;

public class ReBeCenterToManagerMO
{
	/** 服务器信息 */
	@MaybeNull
	ServerInfoDO info;
	/** 区服字典(areaID:gameID) */
	Map<Integer,Integer> areaDic;
}
