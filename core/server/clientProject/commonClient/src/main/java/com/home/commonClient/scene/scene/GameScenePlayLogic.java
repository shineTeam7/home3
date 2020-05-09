package com.home.commonClient.scene.scene;

import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.scene.ScenePlayLogic;
import com.home.commonClient.scene.base.GameScene;

/** 副本类场景玩法逻辑 */
public class GameScenePlayLogic extends ScenePlayLogic
{
	/** 游戏服场景 */
	protected GameScene _gameScene;
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		
		_gameScene=(GameScene)scene;
	}
	
	/** 初始化场景数据 */
	public void initEnterData(SceneEnterData data)
	{
	
	}
}
