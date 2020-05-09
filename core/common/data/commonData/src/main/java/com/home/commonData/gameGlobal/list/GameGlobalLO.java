package com.home.commonData.gameGlobal.list;

import com.home.commonData.gameGlobal.server.GameActivitySPO;
import com.home.commonData.gameGlobal.server.GameFuncSPO;
import com.home.commonData.gameGlobal.server.GameMailSPO;
import com.home.commonData.gameGlobal.server.GameSocialSPO;
import com.home.commonData.gameGlobal.server.GameSystemSPO;
import com.home.commonData.gameGlobal.server.GameTeamSPO;
import com.home.commonData.gameGlobal.server.GameUnionSPO;
import com.home.shineData.support.NecessaryPart;

/** 游戏服全局数据 */
public class GameGlobalLO
{
	/** 系统数据 */
	@NecessaryPart
	GameSystemSPO system;
	/** 社交数据 */
	@NecessaryPart
	GameSocialSPO social;
	/** 通用功能数据 */
	@NecessaryPart
	GameFuncSPO func;
	/** 活动数据 */
	@NecessaryPart
	GameActivitySPO activity;
	/** 邮件数据 */
	@NecessaryPart
	GameMailSPO mail;
	/** 工会数据 */
	GameUnionSPO union;
	/** 队伍数据 */
	GameTeamSPO team;
}
