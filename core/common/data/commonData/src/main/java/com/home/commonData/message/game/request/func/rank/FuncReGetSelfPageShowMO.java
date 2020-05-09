package com.home.commonData.message.game.request.func.rank;

import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.data.BaseDO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

public class FuncReGetSelfPageShowMO extends FuncSMO
{
	/** 页码 */
	int page;
	/** 数据组 */
	@MaybeNull
	List<BaseDO> list;
}
