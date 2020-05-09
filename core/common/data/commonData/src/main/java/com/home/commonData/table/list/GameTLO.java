package com.home.commonData.table.list;

import com.home.commonData.table.table.GlobalTO;
import com.home.commonData.table.table.PlayerNameTO;
import com.home.commonData.table.table.PlayerTO;
import com.home.commonData.table.table.RoleGroupTO;
import com.home.commonData.table.table.RoleSocialTO;
import com.home.shineData.support.NecessaryPart;

/** game服表 */
public class GameTLO
{
	@NecessaryPart
	GlobalTO global;
	@NecessaryPart
	PlayerTO player;
	@NecessaryPart
	RoleSocialTO roleSocial;
	@NecessaryPart
	RoleGroupTO roleGroup;
	@NecessaryPart
	PlayerNameTO playerName;
}
