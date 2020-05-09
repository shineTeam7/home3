package com.home.commonData.message.center.serverRequest.game.func.pageShow;

import com.home.commonData.data.social.rank.RankDO;
import com.home.commonData.data.system.KeyDO;
import com.home.commonData.message.center.serverRequest.game.func.base.FuncPlayerToGameMO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

public class FuncReGetPageShowToPlayerMO extends FuncPlayerToGameMO
{
	/** 页数 */
	int page;
	/** 参数 */
	int arg;
	/** 翻页列表 */
	List<KeyDO> list;
	/** 可能有的排行数据 */
	@MaybeNull
	RankDO rankData;
}
