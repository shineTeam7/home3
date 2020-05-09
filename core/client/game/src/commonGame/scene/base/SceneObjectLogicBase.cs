using System;
using ShineEngine;

/// <summary>
/// 场景对象逻辑体
/// </summary>
public class SceneObjectLogicBase:ILogic
{
	/** 是否生效 */
	public bool enabled=true;

	protected SceneObject _object;

	protected Scene _scene;

	public SceneObjectLogicBase()
	{

	}

	public virtual void setObject(SceneObject obj)
	{
		_object=obj;
	}

	/** 构造 */
	public virtual void construct()
	{

	}

	/** 初始化 */
	public virtual void init()
	{
		_scene=_object.getScene();
	}

	/** 补充初始化 */
	public virtual void afterInit()
	{

	}

	/** 预移除 */
	public virtual void preRemove()
	{

	}

	/** 析构 */
	public virtual void dispose()
	{
		_scene=null;
	}

	/** 刷帧 */
	public virtual void onFrame(int delay)
	{

	}

	/** 每秒 */
	public virtual void onSecond()
	{

	}

	/** 固定更新 */
	public virtual void onFixedUpdate()
	{

	}

	/** 重载配置 */
	public virtual void onReloadConfig()
	{

	}
}