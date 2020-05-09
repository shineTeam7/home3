package com.home.commonData.message.game.response.scene.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.game.response.scene.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

/** 控制单位特殊移动 */
public class CUnitSpecialMoveMO extends CUnitRMO
{
	/** 特殊移动ID */
	int id;
	/** 当前位置 */
	@MaybeNull
	PosDirDO posDir;
	/** 参数组 */
	@MaybeNull
	int[] args;
}
