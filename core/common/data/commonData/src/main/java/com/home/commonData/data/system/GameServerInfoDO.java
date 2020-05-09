package com.home.commonData.data.system;

import java.util.List;

/** 游戏服信息数据 */
public class GameServerInfoDO extends ServerInfoDO
{
	/** 是否辅助服(跨服) */
	boolean isAssist;
	/** 承载的区服列表 */
	public List<Integer> areaIDList;
}
