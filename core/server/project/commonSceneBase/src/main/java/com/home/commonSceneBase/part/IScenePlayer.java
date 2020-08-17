package com.home.commonSceneBase.part;

import com.home.commonBase.data.role.RoleShowData;
import com.home.shine.net.socket.BaseSocket;

/** 场景Player基类 */
public interface IScenePlayer
{
	RoleShowData createRoleShowData();
	
	boolean isSocketReady();
	
	BaseSocket getSocket();
	/** playerID */
	long getPlayerID();
}
