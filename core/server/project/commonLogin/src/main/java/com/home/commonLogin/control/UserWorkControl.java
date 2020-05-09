package com.home.commonLogin.control;

import com.home.commonBase.data.login.PlayerCreatedWData;
import com.home.commonBase.data.login.PlayerDeletedWData;
import com.home.commonBase.data.system.UserWorkData;
import com.home.commonBase.table.table.UserTable;

public class UserWorkControl extends BaseUserWorkControl
{
	/** 注册 */
	@Override
	protected void regist()
	{
		registOne(PlayerCreatedWData.dataID,this::playerCreated);
		registOne(PlayerDeletedWData.dataID,this::playerDeleted);
	}
	
	protected void playerCreated(UserTable table,UserWorkData wData)
	{
		PlayerCreatedWData data=(PlayerCreatedWData)wData;
		
		table.addPlayerID(data.playerID);
	}
	
	protected void playerDeleted(UserTable table,UserWorkData wData)
	{
		PlayerDeletedWData data=(PlayerDeletedWData)wData;
		
		table.removePlayerID(data.playerID);
	}
}
