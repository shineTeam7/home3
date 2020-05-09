package com.home.commonData.centerGlobal.list;

import com.home.commonData.centerGlobal.server.CenterActivitySPO;
import com.home.commonData.centerGlobal.server.CenterFuncSPO;
import com.home.commonData.centerGlobal.server.CenterMailSPO;
import com.home.commonData.centerGlobal.server.CenterSocialSPO;
import com.home.commonData.centerGlobal.server.CenterSystemSPO;
import com.home.commonData.centerGlobal.server.CenterUnionSPO;
import com.home.shineData.support.NecessaryPart;

/** 中心服全局数据 */
public class CenterGlobalLO
{
	/** 系统数据 */
	@NecessaryPart
	CenterSystemSPO system;
	/** 社交数据 */
	@NecessaryPart
	CenterSocialSPO social;
	/** 通用功能数据 */
	@NecessaryPart
	CenterFuncSPO func;
	/** 活动数据 */
	@NecessaryPart
	CenterActivitySPO activity;
	/** 邮件数据 */
	@NecessaryPart
	CenterMailSPO mail;
	/** 中心服宗门数据 */
	CenterUnionSPO union;
}
