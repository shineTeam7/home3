package com.home.commonBase.scene.base;

public class SceneObjectLogicBase implements ILogic
{
	/** 是否生效 */
	public boolean enabled=true;
	
	protected SceneObject _object;
	
	protected Scene _scene;
	
	/** 设置对象 */
	public void setObject(SceneObject obj)
	{
		_object=obj;
	}

	@Override
	public void construct()
	{
		
	}

	@Override
	public void init()
	{
		_scene=_object.getScene();
	}
	
	/** 初始化后续 */
	public void afterInit()
	{
		
	}

	@Override
	public void dispose()
	{
		_scene=null;
	}
	
	/** 预析构(角色用) */
	public void preRemove()
	{
	
	}

	@Override
	public void onFrame(int delay)
	{
		
	}
	
	/** 每秒 */
	public void onSecond(int delay)
	{
	
	}
	
	/** 重载配置 */
	public void onReloadConfig()
	{
	
	}
}
