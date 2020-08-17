package com.home.commonData.message.sceneBase.request.syncScene;

import com.home.commonData.data.scene.fight.FrameSyncDO;
import com.home.commonData.message.sceneBase.request.base.SceneSMO;

import java.util.List;

/** 帧同步起始消息 */
public class FrameSyncStartMO extends SceneSMO
{
	/** 数据组 */
	List<FrameSyncDO> list;
}
