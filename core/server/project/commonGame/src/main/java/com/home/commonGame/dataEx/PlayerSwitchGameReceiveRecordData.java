package com.home.commonGame.dataEx;

import com.home.commonBase.data.login.PlayerSwitchGameData;
import com.home.commonBase.data.system.PlayerPrimaryKeyData;
import com.home.commonBase.part.player.list.PlayerListData;

/** 角色切换服务器接收记录数据 */
public class PlayerSwitchGameReceiveRecordData
{
	public int token;
	/** 主键数据 */
	public PlayerPrimaryKeyData keyData;
	/** 列表数据 */
	public PlayerListData listData;
	/** 切换数据 */
	public PlayerSwitchGameData switchData;
	/** 倒计时(s) */
	public int timeOut;
}
