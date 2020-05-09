package com.home.commonData.table.list;

import com.home.commonData.table.table.ActivationCodeTO;
import com.home.commonData.table.table.PlayerNameTO;
import com.home.commonData.table.table.GlobalTO;
import com.home.commonData.table.table.RoleSocialTO;
import com.home.commonData.table.table.ServerTO;
import com.home.commonData.table.table.UnionNameTO;
import com.home.commonData.table.table.UserTO;
import com.home.commonData.table.table.WhiteListTO;
import com.home.shineData.support.NecessaryPart;

/** 中心服表 */
public class CenterTLO
{
	@NecessaryPart
	GlobalTO global;
	@NecessaryPart
	ServerTO server;
	@NecessaryPart
	UserTO user;
	@NecessaryPart
	RoleSocialTO roleSocial;
	@NecessaryPart
	WhiteListTO whiteList;
	@NecessaryPart
	ActivationCodeTO activationCode;
	
	PlayerNameTO playerName;
	UnionNameTO unionName;
}
