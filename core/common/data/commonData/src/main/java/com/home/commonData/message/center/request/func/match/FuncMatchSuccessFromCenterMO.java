package com.home.commonData.message.center.request.func.match;

import com.home.commonData.data.scene.match.PlayerMatchDO;
import com.home.commonData.message.center.request.func.base.FuncFromCenterSMO;

/** 匹配成功到game */
public class FuncMatchSuccessFromCenterMO extends FuncFromCenterSMO
{
	/** 匹配序号 */
	int index;
	/** 匹配数据组 */
	PlayerMatchDO[] matchDatas;
}
