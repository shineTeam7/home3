package com.home.commonData.message.sceneBase.request.unit;

import com.home.commonData.data.scene.base.DirDO;
import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.sceneBase.request.base.SceneSMO;

/** 刷新简版单位位置消息 */
public class RefreshSimpleUnitPosMO extends SceneSMO
{
	int instanceID;
	
	PosDO pos;
	
	DirDO dir;
}
