package com.home.commonData.data.system;

import java.util.List;

/** 简版游戏服信息数据 */
public class GameServerSimpleInfoDO extends ServerSimpleInfoDO
{
	/** 是否辅助服(跨服) */
	boolean isAssist;
	/** 是否必须 */
	boolean isNecessary;
	/** 承载的区服列表 */
	List<Integer> areaIDList;
	/** 所属国家id */
	int countryID;
}
