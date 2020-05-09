package com.home.commonData.message.game.serverRequest.game.func.pageShow;

import com.home.commonData.data.social.rank.RankDO;
import com.home.commonData.data.system.KeyDO;
import com.home.commonData.message.game.serverRequest.game.func.base.FuncPlayerGameToGameMO;
import com.home.shineData.support.MaybeNull;

import java.util.List;

public class FuncReGetPageShowGameToPlayerMO extends FuncPlayerGameToGameMO
{
	/** 页数 */
	int page;
	
	int arg;
	/** 翻页列表 */
	List<KeyDO> list;
	/** 可能有的排行数据 */
	@MaybeNull
	RankDO rankData;
}
