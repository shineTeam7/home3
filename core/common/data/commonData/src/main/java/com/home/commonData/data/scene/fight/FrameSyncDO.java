package com.home.commonData.data.scene.fight;

import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 帧同步数据 */
public class FrameSyncDO
{
	/** 帧序号 */
	int index;
	/** 指令组 */
	@MaybeNull
	List<FrameSyncCommandDO> commands;
}
