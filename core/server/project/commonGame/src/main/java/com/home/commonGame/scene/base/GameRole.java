package com.home.commonGame.scene.base;

import com.home.commonBase.scene.base.Role;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.role.RoleAttributeLogic;
import com.home.commonGame.part.player.Player;
import com.home.commonSceneBase.scene.base.BRole;
import com.home.commonSceneBase.scene.role.BRoleAttributeLogic;
import com.home.commonGame.scene.scene.GameSceneInOutLogic;
import com.home.shine.net.base.BaseRequest;
import com.home.shine.net.socket.BaseSocket;

public class GameRole extends BRole
{
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	/** 获取玩家(如玩家离线，则不存在) */
	public Player getPlayer()
	{
		return _gameScene.gameInOut.getPlayer(playerID);
	}
	
	@Override
	public BaseSocket getSocket()
	{
		Player player=getPlayer();
		
		if(player!=null)
			return player.system.socket;
		
		return null;
	}
}
