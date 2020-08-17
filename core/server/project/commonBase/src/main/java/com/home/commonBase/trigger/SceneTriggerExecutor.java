package com.home.commonBase.trigger;

import com.home.commonBase.constlist.generate.TriggerEventType;
import com.home.commonBase.data.scene.base.DirData;
import com.home.commonBase.data.scene.base.PosData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.data.trigger.TriggerObjData;

/** 场景触发 */
public class SceneTriggerExecutor extends TriggerExecutor
{
	/** 场景 */
	protected Scene _scene;
	
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	/** 获取所在场景 */
	public Scene getScene()
	{
		return _scene;
	}
	
	/** 获取单位快捷方式 */
	public Unit getUnit(TriggerObjData obj,TriggerArg arg)
	{
		return (Unit)getObj(obj,arg);
	}
	
	/** 获取点快捷方式 */
	public PosData getPos(TriggerObjData obj,TriggerArg arg)
	{
		return (PosData)getObj(obj,arg);
	}
	
	/** 获取朝向快捷方式 */
	public DirData getDir(TriggerObjData obj,TriggerArg arg)
	{
		return (DirData)getObj(obj,arg);
	}
	
	/** 创建事件(只创建) */
	protected TriggerEvent toCreateEvent()
	{
		return new SceneTriggerEvent();
	}
	
	@Override
	protected void initEvent(TriggerEvent event)
	{
		super.initEvent(event);
		
		switch(event.type)
		{
			case TriggerEventType.OnUnitMove:
			case TriggerEventType.OnUnitBeDamage:
			{
				((SceneTriggerEvent)event).triggerUnit=(Unit)event.args[0];
			}
				break;
		}
	}
	
}
