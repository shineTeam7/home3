package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.DirDO;
import com.home.commonData.message.sceneBase.request.base.UnitSMO;
import com.home.shineData.support.MaybeNull;

/** 单位移动朝向消息 */
public class UnitMoveDirMO extends UnitSMO
{
	/** 移动类型 */
	int type;
	/** 目标朝向 */
	DirDO dir;
	/** 实际移动朝向 */
	@MaybeNull
	DirDO realMoveDir;
	/** 实际移动速度比率 */
	int realMoveSpeedRatio;
}
