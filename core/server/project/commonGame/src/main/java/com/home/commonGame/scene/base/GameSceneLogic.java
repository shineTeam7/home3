package com.home.commonGame.scene.base;

import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.SceneLogicBase;

public abstract class GameSceneLogic extends SceneLogicBase
{
	/** 游戏服场景 */
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
}
