package com.home.commonData.data.login;

import com.home.commonData.data.system.ServerInfoDO;
import com.home.shineData.support.MaybeNull;
import com.home.shineData.support.OnlyS;

import java.util.Map;

/** 中心服初始化服务器数据 */
@OnlyS
public class CenterInitServerDO
{
	/** 服务器信息 */
	ServerInfoDO info;
	/** 区服字典(areaID:gameID) */
	Map<Integer,Integer> areaDic;
}
