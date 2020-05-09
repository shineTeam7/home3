package com.home.commonData.message.game.request.scene.syncScene;

import com.home.commonData.data.scene.fight.FrameSyncDO;
import com.home.commonData.message.game.request.scene.base.SceneSMO;
import com.home.shineData.support.MessageDontCopy;

/** 帧同步每帧消息 */
@MessageDontCopy
public class FrameSyncFrameMO extends SceneSMO
{
	/** 帧数据 */
	FrameSyncDO frame;
}
