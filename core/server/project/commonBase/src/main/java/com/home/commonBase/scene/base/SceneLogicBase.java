package com.home.commonBase.scene.base;

/** 场景逻辑体基类 */
public abstract class SceneLogicBase implements ILogic
{
	/** 是否生效 */
	public boolean enabled=true;
	
	protected Scene _scene;
	
	/** 设置场景 */
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	/** 每秒 */
	public void onSecond(int delay)
	{

	}
	
	/** 每秒无视暂停 */
	public void onSecondNoMatterPause()
	{

	}
	
	/** 添加单位 */
	public void onAddUnit(Unit unit)
	{
		
	}
	
	/** 移除单位 */
	public void onRemoveUnit(Unit unit)
	{
		
	}
	
	/** 初始化后 */
	public void afterInit()
	{
	
	}
}
