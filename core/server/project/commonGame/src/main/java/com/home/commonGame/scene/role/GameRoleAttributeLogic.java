package com.home.commonGame.scene.role;

import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonGame.net.request.scene.role.RoleRefreshAttributeRequest;
import com.home.shine.support.collection.IntIntMap;

/** 游戏角色逻辑属性 */
public class GameRoleAttributeLogic extends RoleAttributeLogic
{
	/** 推送自己属性 */
	public void sendSelfAttribute(IntIntMap dic)
	{
		_role.send(RoleRefreshAttributeRequest.create(_role.playerID,dic));
	}
	
	/** 推送别人属性 */
	public void sendOtherAttribute(IntIntMap dic)
	{
		_role.radioMessage(RoleRefreshAttributeRequest.create(_role.playerID,dic),false);
	}
}
