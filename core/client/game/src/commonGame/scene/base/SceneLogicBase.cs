using System;
using ShineEngine;

/// <summary>
/// 场景逻辑体基类
/// </summary>
public abstract class SceneLogicBase:ILogic
{
	/** 是否生效 */
	public bool enabled=true;

	protected Scene _scene;

	public SceneLogicBase()
	{

	}

	public void setScene(Scene scene)
	{
		_scene=scene;
	}

	public virtual void construct()
	{

	}

	/** 场景刚加载好 */
	public virtual void onSceneLoad()
	{

	}

	public virtual void init()
	{

	}

	public virtual void dispose()
	{

	}

	public virtual void onFrame(int delay)
	{
		
	}

	/** 固定更新 */
	public virtual void onFixedUpdate()
	{

	}

	/** 每秒 */
	public virtual void onSecond()
	{

	}

	/** 添加单位 */
	public virtual void onAddUnit(Unit unit)
	{

	}

	/** 移除单位(从字典移除前) */
	public virtual void onRemoveUnit(Unit unit)
	{

	}

	/** 初始化英雄后 */
	public virtual void afterHero()
	{

	}

	/** 准备移除时 */
	public virtual void preRemove()
	{

	}
}