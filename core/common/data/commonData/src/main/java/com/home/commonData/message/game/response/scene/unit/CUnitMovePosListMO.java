package com.home.commonData.message.game.response.scene.unit;

import com.home.commonData.data.scene.base.PosDO;
import com.home.commonData.message.game.response.scene.base.CUnitRMO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 客户端单位移动到点组消息 */
public class CUnitMovePosListMO extends CUnitRMO
{
	/** 移动类型 */
	int type;
	/** 当前位置 */
	@MaybeNull
	PosDO nowPos;
	/** 位置组 */
	List<PosDO> targets;
}
