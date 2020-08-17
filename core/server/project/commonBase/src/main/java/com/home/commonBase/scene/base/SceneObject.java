package com.home.commonBase.scene.base;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.SList;

/** 场景对象 */
public class SceneObject implements ILogic
{
	/** 是否生效(反之就是被移除) */
	public boolean enabled=false;
	
	/** 场景 */
	protected Scene _scene;
	
	/** 逻辑体组 */
	protected SList<SceneObjectLogicBase> _logics=new SList<>(SceneObjectLogicBase[]::new);
	
	/** 是否需要移除 */
	private boolean _needRemove=false;
	
	/** 添加logic */
	public void addLogic(SceneObjectLogicBase logic)
	{
		if(logic==null)
		{
			Ctrl.throwError("logic不能为空");
			return;
		}
		
		_logics.add(logic);
		logic.setObject(this);
	}
	
	/** 移除logic */
	public void removeLogic(SceneObjectLogicBase logic)
	{
		int index=_logics.indexOf(logic);
		
		if(index!=-1)
		{
			_logics.remove(index);
		}
	}
	
	/** 设置场景 */
	public void setScene(Scene scene)
	{
		_scene=scene;
	}
	
	/** 获取场景 */
	public Scene getScene()
	{
		return _scene;
	}
	
	//logics
	
	/** 注册逻辑体 */
	protected void registLogics()
	{
		
	}
	
	//logics
	
	/** 构造 */
	@Override
	public void construct()
	{
		registLogics();
		
		SceneObjectLogicBase[] values=_logics.getValues();
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			values[i].construct();
		}
	}
	
	/** 初始化 */
	@Override
	public void init()
	{
		preInit();
		
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		int len=_logics.size();
		
		for(int i=0;i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.init();
			}
		}
		
		for(int i=0;i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.afterInit();
			}
		}
	}
	
	/** 预备初始化(可做额外的addLogic) */
	protected void preInit()
	{
	
	}
	
	/** 预移除(角色用) */
	public void preRemove()
	{
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.preRemove();
			}
		}
	}
	
	/** 析构 */
	@Override
	public void dispose()
	{
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.dispose();
			}
			else
			{
				logic.enabled=true;
			}
		}
		
		_needRemove=false;
		
		afterDispose();
	}
	
	/** 析构后(可做额外的removeLogic) */
	protected void afterDispose()
	{
	
	}
	
	/** 刷帧 */
	@Override
	public void onFrame(int delay)
	{
		if(!enabled)
			return;
		
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if(enabled)
			{
				if((logic=values[i]).enabled)
				{
					logic.onFrame(delay);
				}
			}
		}
		
		if(_needRemove)
		{
			_needRemove=false;
			removeAbs();
		}
	}
	
	public void onSecond(int delay)
	{
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if(!enabled)
				return;
			
			if((logic=values[i]).enabled)
			{
				logic.onSecond(delay);
			}
		}
	}
	
	public void onReloadConfig()
	{
		SceneObjectLogicBase[] values=_logics.getValues();
		SceneObjectLogicBase logic;
		
		for(int i=0, len=_logics.size();i<len;++i)
		{
			if((logic=values[i]).enabled)
			{
				logic.onReloadConfig();
			}
		}
	}
	
	/** 下帧移除(使用在单位Logic的onFrame中) */
	public void removeLater()
	{
		_needRemove=true;
	}
	
	/** 立即移除 */
	public void removeAbs()
	{
	
	}
}
