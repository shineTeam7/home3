using System;
using ShineEngine;

/// <summary>
/// 场景对象
/// </summary>
public class SceneObject:ILogic
{
	/** 是否生效(反之就是被移除) */
	public bool enabled=false;

	/** 所在场景 */
	protected Scene _scene;

	/** 逻辑体组 */
	protected SList<SceneObjectLogicBase> _logics=new SList<SceneObjectLogicBase>();

	/** 是否需要移除 */
	private bool _needRemove=false;

	public SceneObject()
	{
	}

	/** 添加logic */
	public void addLogic(SceneObjectLogicBase logic)
	{
		_logics.add(logic);
		logic.setObject(this);
	}

	/** 移除logic */
	public void removeLogic(SceneObjectLogicBase logic)
	{
		_logics.removeObj(logic);
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
	protected virtual void registLogics()
	{
	}


	/** 构造 */
	public void construct()
	{
		registLogics();

		SList<SceneObjectLogicBase> logics=_logics;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			logics[i].construct();
		}
	}

	/** 初始化 */
	public void init()
	{
		preInit();

		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.init();
			}
		}

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.afterInit();
			}
		}
	}

	/** 预备初始化 */
	protected virtual void preInit()
	{

	}

	/** 预移除 */
	public void preRemove()
	{
		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.preRemove();
			}
		}
	}

	/** 析构 */
	public virtual void dispose()
	{
		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.dispose();
			}
			else
			{
				logic.enabled=true;
			}
		}

		_needRemove=false;
	}

	/** 刷帧 */
	public virtual void onFrame(int delay)
	{
		if(!enabled)
			return;

		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if(enabled)
			{
				if((logic=logics[i]).enabled)
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

	public void onSecond()
	{
		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if(!enabled)
				return;

			if((logic=logics[i]).enabled)
			{
				logic.onSecond();
			}
		}
	}

	/** 刷帧 */
	public virtual void onFixedUpdate()
	{
		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if(!enabled)
				return;

			if((logic=logics[i]).enabled)
			{
				logic.onFixedUpdate();
			}
		}
	}

	public virtual void onReloadConfig()
	{
		SList<SceneObjectLogicBase> logics=_logics;
		SceneObjectLogicBase logic;

		for(int i=0,len=logics.Count;i<len;++i)
		{
			if((logic=logics[i]).enabled)
			{
				logic.onReloadConfig();
			}
		}
	}

	/** 下帧移除 */
	public void removeLater()
	{
		//先标记失效
		enabled=false;
		_needRemove=true;
	}

	/** 立即移除 */
	public virtual void removeAbs()
	{

	}
}