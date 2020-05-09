package com.home.commonGame.dataEx;

import com.home.commonBase.part.player.list.PlayerListData;
import com.home.commonGame.part.player.Player;
import com.home.shine.support.collection.IntObjectMap;
import com.home.shine.support.collection.LongObjectMap;
import com.home.shine.support.collection.SList;

public class SaveSwitchPlayerTempData
{
	/** 角色数目 */
	public int playerNum;
	/** 需要处理的角色组 */
	public SList<Player> players=new SList<>(Player[]::new);
	/** 角色按区服划分组 */
	public IntObjectMap<LongObjectMap<PlayerListData>> playersForGameDic=new IntObjectMap<>(LongObjectMap[]::new);
	/** 本次需要 */
	public int index;
	/** 超时时间(s) */
	public int timeOut;
}
