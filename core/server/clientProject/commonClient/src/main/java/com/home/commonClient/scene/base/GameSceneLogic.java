package com.home.commonClient.scene.base;

import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.SceneLogicBase;

public abstract class GameSceneLogic extends SceneLogicBase
{
	/** 游戏场景 */
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
}
