package com.home.commonData.message.sceneBase.response.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.sceneBase.response.base.CUnitRMO;
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