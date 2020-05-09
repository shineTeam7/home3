package com.home.commonData.message.game.request.func.rank;

import com.home.commonData.data.system.KeyDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.support.MaybeNull;
import com.home.shineData.support.MessageDontCopy;

import java.util.Map;
import java.util.Set;

/** 功能-回复查询每页排行 */
@MessageDontCopy
public class FuncReGetPageShowMO extends FuncSMO
{
	/** 页码 */
	int page;
	/** 参数 */
	int arg;
	/** 数据组 */
	@MaybeNull
	Map<Integer,KeyDO> dic;
	/** 改变页码组 */
	@MaybeNull
	Set<Integer> changePageSet;
}
