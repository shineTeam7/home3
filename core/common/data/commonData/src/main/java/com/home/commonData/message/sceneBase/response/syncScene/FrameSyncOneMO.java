package com.home.commonData.message.sceneBase.response.syncScene;

import com.home.commonData.data.scene.fight.FrameSyncCommandDO;
import com.home.commonData.message.sceneBase.response.base.SceneRMO;

/** 帧同步单个玩家消息 */
public class FrameSyncOneMO extends SceneRMO
{
	/** 指令 */
	FrameSyncCommandDO command;
}
