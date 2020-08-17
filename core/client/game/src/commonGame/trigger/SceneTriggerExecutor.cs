using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class SceneTriggerExecutor:TriggerExecutor
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
	protected override TriggerEvent toCreateEvent()
	{
		return new SceneTriggerEvent();
	}


	protected override void initEvent(TriggerEvent evt)
	{
		base.initEvent(evt);

		switch(evt.type)
		{
			case TriggerEventType.OnUnitMove:
			case TriggerEventType.OnUnitBeDamage:
			{
				((SceneTriggerEvent)evt).triggerUnit=(Unit)evt.args[0];
			}
				break;
		}
	}
}