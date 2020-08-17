package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.DirDO;
import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

/** 客户端单位朝向移动消息 */
public class CUnitMoveDirMO extends CUnitRMO
{
	/** 移动类型 */
	int type;
	/** 当前位置 */
	@MaybeNull
	PosDO pos;
	/** 目标朝向 */
	DirDO dir;
	@MaybeNull
	/** 实际移动方向 */
	DirDO realDir;
	/** 实际速度比率 */
	int realSpeedRatio=-1;
}
