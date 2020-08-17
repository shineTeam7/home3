package com.home.commonData.message.sceneBase.request.scene;

import com.home.commonData.data.scene.unit.UnitDO;
import com.home.commonData.message.sceneBase.request.base.SceneSMO;

import java.util.List;

/** AOI灯塔刷新消息(删除一组+添加一组) */
public class AOITowerRefreshMO extends SceneSMO
{
	/** 移除单位组 */
	List<Integer> removeUnits;
	/** 添加单位组 */
	List<UnitDO> addUnits;
}
