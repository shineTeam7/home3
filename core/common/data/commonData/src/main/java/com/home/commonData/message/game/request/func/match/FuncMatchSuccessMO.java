package com.home.commonData.message.game.request.func.match;

import com.home.commonData.data.scene.match.PlayerMatchDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.support.MessageDontCopy;

/** 匹配成功消息 */
@MessageDontCopy
public class FuncMatchSuccessMO extends FuncSMO
{
	/** 匹配序号 */
	int index;
	/** 匹配数据组 */
	PlayerMatchDO[] matchDatas;
}
