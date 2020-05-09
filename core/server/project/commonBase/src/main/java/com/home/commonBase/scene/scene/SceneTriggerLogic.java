package com.home.commonBase.scene.scene;

import com.home.commonBase.constlist.generate.TriggerGroupType;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.trigger.SceneTriggerExecutor;

/** 触发器逻辑 */
public class SceneTriggerLogic extends SceneLogicBase
{
	/** 执行器 */
	private SceneTriggerExecutor _executor;
	
	@Override
	public void construct()
	{
		_executor=BaseC.factory.createSceneTriggerExecutor();
		_executor.setScene(_scene);
		_executor.construct();
	}
	
	@Override
	public void init()
	{
		_executor.init(TriggerGroupType.Scene,_scene.getConfig().triggerID);
	}
	
	@Override
	public void dispose()
	{
		_executor.dispose();
	}
	
	@Override
	public void onFrame(int delay)
	{
		_executor.onFrame(delay);
	}
	
	/** 发生事件 */
	public void triggerEvent(int type)
	{
		if(!_executor.isEnable())
			return;
		
		_executor.triggerEvent(type);
	}
	
	/** 发生事件 */
	public void triggerEvent(int type,Object...args)
	{
		if(!_executor.isEnable())
			return;
		
		_executor.triggerEvent(type,args);
	}
}
