package com.home.commonGame.scene.base;

import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.scene.scene.SceneAOILogic;
import com.home.commonBase.scene.scene.SceneInOutLogic;
import com.home.commonBase.scene.scene.SceneMethodLogic;
import com.home.commonBase.scene.scene.SceneRoleLogic;
import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.global.GameC;
import com.home.commonGame.scene.scene.GameSceneInOutLogic;
import com.home.commonGame.scene.scene.GameSceneMethodLogic;
import com.home.commonGame.scene.scene.GameSceneRoleLogic;
import com.home.commonSceneBase.scene.base.BScene;
import com.home.commonSceneBase.scene.scene.SceneAOIAllLogic;

/** 场景 */
public class GameScene extends BScene
{
	/** 游戏进出逻辑 */
	public GameSceneInOutLogic gameInOut;
	
	/** 游戏玩法逻辑 */
	public GameSceneMethodLogic gameMethod;
	
	/** 游戏角色逻辑 */
	public GameSceneRoleLogic gameRole;
	
	
	@Override
	protected void registLogics()
	{
		super.registLogics();
		
		gameInOut=(GameSceneInOutLogic)inout;
		gameMethod=(GameSceneMethodLogic)method;
		gameRole=(GameSceneRoleLogic)role;
	}
	
	//logics
	
	/** 创建进出逻辑 */
	protected SceneInOutLogic createInOutLogic()
	{
		return new GameSceneInOutLogic();
	}
	
	@Override
	protected SceneRoleLogic createRoleLogic()
	{
		return new GameSceneRoleLogic();
	}
	
	@Override
	protected SceneAOILogic createAOILogic()
	{
		return new SceneAOIAllLogic();
	}
	
	@Override
	protected SceneMethodLogic createMethodLogic()
	{
		return new GameSceneMethodLogic();
	}
	
	/** 获取执行器 */
	public LogicExecutor getGameExecutor()
	{
		return (LogicExecutor)_executor;
	}
	
	@Override
	public void dispose()
	{
		//清人
		gameInOut.clearAllPlayer();
		
		super.dispose();
	}
	
	//接口组
	
	@Override
	protected void makeSceneLocationData(SceneLocationData data)
	{
		super.makeSceneLocationData(data);
		data.serverID=GameC.app.id;
	}
	
	/** 移除该场景 */
	@Override
	public void removeScene()
	{
		//析构过了
		if(!_inited)
			return;
		
		getGameExecutor().removeScene(instanceID);
	}
	
}
