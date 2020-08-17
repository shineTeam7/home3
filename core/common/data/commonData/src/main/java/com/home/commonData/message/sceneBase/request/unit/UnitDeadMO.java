package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.message.sceneBase.request.base.UnitSMO;

/** 单位死亡消息 */
public class UnitDeadMO extends UnitSMO
{
	/** 击杀者实例ID */
	int attackerInstanceID;
	/** 死亡类型 */
	int type;
	/** 是否真死亡(否则就是死后复活) */
	boolean isReal;
}
