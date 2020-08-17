package com.home.commonData.message.sceneBase.request.scene;

import com.home.commonData.data.scene.unit.UnitSimpleDO;
import com.home.commonData.message.sceneBase.request.base.SceneSMO;
import com.home.shineData.support.MaybeNull;

/** 添加绑定视野单位 */
public class AddBindVisionUnitMO extends SceneSMO
{
	/** 实例ID */
	int instanceID;
	/** 单位数据(如为空则为在视野中) */
	@MaybeNull
	UnitSimpleDO data;
}
