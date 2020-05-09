package com.home.commonGame.control;

import com.home.commonBase.control.BaseVersionControl;
import com.home.commonBase.data.system.SaveVersionData;
import com.home.commonGame.part.player.Player;

/** 角色版本控制 */
public class PlayerVersionControl extends BaseVersionControl<Player>
{
	public void init()
	{
	
	}
	
	@Override
	protected SaveVersionData getVersionData(Player me)
	{
		return me.system.getPartData().version;
	}
}
