package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.message.sceneBase.request.base.UnitSMO;

/** 单位释放技能结束(强制结束) */
public class UnitSkillOverMO extends UnitSMO
{
	/** 是否需要中断指令 */
	boolean needBreak;
}
