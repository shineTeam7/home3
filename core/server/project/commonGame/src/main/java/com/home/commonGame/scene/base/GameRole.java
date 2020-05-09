package com.home.commonGame.scene.base;

import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonGame.net.base.GameRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.role.GameRoleAttributeLogic;
import com.home.commonGame.scene.scene.GameSceneInOutLogic;
import com.home.shine.net.base.BaseRequest;

public class GameRole extends Role
{
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	@Override
	protected RoleAttributeLogic createRoleAttributeLogic()
	{
		return new GameRoleAttributeLogic();
	}
	
	/** 获取玩家(如玩家离线，则不存在) */
	public Player getPlayer()
	{
		return _gameScene.gameInOut.getPlayer(playerID);
	}
	
	/** 推送消息 */
	@Override
	public void send(BaseRequest request)
	{
		Player player;
		
		if((player=getPlayer())!=null)
		{
			player.send(request);
		}
	}
	
	@Override
	public void radioMessage(BaseRequest request,boolean needSelf)
	{
		GameSceneInOutLogic gameInOut=_gameScene.gameInOut;
		
		request.write();
		
		_scene.role.getRoleDic().forEachValue(v->
		{
			if(v.playerID!=playerID || needSelf)
			{
				Player player;
				if((player=gameInOut.getPlayer(v.playerID))!=null)
				{
					player.send(request);
				}
			}
		});
	}
}
