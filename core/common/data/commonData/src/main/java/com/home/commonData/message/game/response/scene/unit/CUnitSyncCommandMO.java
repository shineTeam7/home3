package com.home.commonData.message.game.response.scene.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.game.response.scene.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

/** 单位同步参数指令 */
public class CUnitSyncCommandMO extends CUnitRMO
{
	/** 当前位置 */
	@MaybeNull
	PosDirDO posDir;
	/** 指令 */
	int type;
	/** 整形参数组 */
	@MaybeNull
	int[] ints;
	/** float参数组 */
	@MaybeNull
	float[] floats;
}