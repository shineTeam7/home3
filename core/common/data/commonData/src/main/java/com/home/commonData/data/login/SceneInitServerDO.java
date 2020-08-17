package com.home.commonData.data.login;

import com.home.commonData.data.system.ServerInfoDO;

import java.util.Map;

public class SceneInitServerDO
{
	/** 本登录服信息 */
	ServerInfoDO info;
	/** 区服字典(areaID:gameID) */
	Map<Integer,Integer> areaDic;
}
