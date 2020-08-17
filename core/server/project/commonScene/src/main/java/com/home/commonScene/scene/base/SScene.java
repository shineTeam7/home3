package com.home.commonScene.scene.base;

import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.scene.scene.SceneInOutLogic;
import com.home.commonBase.scene.scene.SceneMethodLogic;
import com.home.commonScene.control.SceneLogicExecutor;
import com.home.commonScene.global.SceneC;
import com.home.commonScene.scene.scene.SSceneInoutLogic;
import com.home.commonScene.scene.scene.SSceneMethodLogic;
import com.home.commonSceneBase.scene.base.BScene;

/** 场景服场景 */
public class SScene extends BScene
{
	public SSceneInoutLogic sInOut;
	
	@Override
	protected void registLogics()
	{
		super.registLogics();
		
		sInOut=(SSceneInoutLogic)inout;
	}
	
	@Override
	public void dispose()
	{
		//清人
		sInOut.clearAllPlayer();
		
		super.dispose();
	}
	
	@Override
	protected void makeSceneLocationData(SceneLocationData data)
	{
		super.makeSceneLocationData(data);
		data.serverID=SceneC.app.id;
	}
	
	@Override
	protected SceneInOutLogic createInOutLogic()
	{
		return new SSceneInoutLogic();
	}
	
	@Override
	protected SceneMethodLogic createMethodLogic()
	{
		return new SSceneMethodLogic();
	}
	
	/** 获取执行器 */
	public SceneLogicExecutor getSceneExecutor()
	{
		return (SceneLogicExecutor)_executor;
	}
	
	/** 移除该场景 */
	@Override
	public void removeScene()
	{
		//析构过了
		if(!_inited)
			return;
		
		getSceneExecutor().removeScene(instanceID);
	}
}
