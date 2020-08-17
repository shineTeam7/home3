package com.home.commonData.message.sceneBase.request.scene;

import com.home.commonData.message.sceneBase.request.base.SceneSMO;
import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MessageDontCopy;

/** 推送场景广播消息 */
@MessageDontCopy
public class SceneRadioMO extends SceneSMO
{
	/** 数据 */
	BaseDO data;
}
