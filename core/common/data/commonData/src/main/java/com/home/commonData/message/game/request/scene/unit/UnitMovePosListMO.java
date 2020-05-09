package com.home.commonData.message.game.request.scene.unit;

import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.game.request.scene.base.UnitSMO;

import java.util.List;

/** 单位移动点组消息 */
public class UnitMovePosListMO extends UnitSMO
{
	/** 移动类型 */
	int type;
	/** 位置组 */
	List<PosDO> targets;
	/** 服务器首点移动时间(同步用) */
	int moveTime;
}
