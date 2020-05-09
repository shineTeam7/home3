package com.home.commonData.message.game.request.func.rank.subsection;

import com.home.commonData.data.system.KeyDO;
import com.home.commonData.message.game.request.func.base.FuncSMO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

/** 推送获取分段翻页显示内容，无缓存方式 */
public class FuncReGetSubsectionPageShowListMO extends FuncSMO
{
	/** 大组index */
	int subsectionIndex;
	/** 小组index */
	int subsectionSubIndex;
	/** 页码 */
	int page;
	/** 参数 */
	int arg;
	/** 数据组 */
	@MaybeNull
	List<KeyDO> list;
}
