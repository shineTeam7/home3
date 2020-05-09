package com.home.commonData.message.game.response.func.rank.subsection;

import com.home.commonData.message.game.response.func.base.FuncRMO;
import com.home.shineData.support.MessageUseMainThread;

/** 获取每页数据(主线程) */
@MessageUseMainThread
public class FuncGetSubsectionPageShowMO extends FuncRMO
{
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
	/** 页码 */
	int page;
	/** 参数(默认0) */
	int arg;
}
