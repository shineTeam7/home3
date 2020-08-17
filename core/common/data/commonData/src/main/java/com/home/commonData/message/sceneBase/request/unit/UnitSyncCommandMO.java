package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.PosDirDO;
import com.home.commonData.message.sceneBase.request.base.UnitSMO;
import com.home.shineData.support.MessageDontCopy;

/** 单位同步指令消息 */
@MessageDontCopy
public class UnitSyncCommandMO extends UnitSMO
{
	/** 当前位置 */
	PosDirDO posDir;
	/** 指令 */
	int type;
	/** 整形参数组 */
	int[] ints;
	/** float参数组 */
	float[] floats;
}