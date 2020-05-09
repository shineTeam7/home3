using System;
using ShineEngine;
using UnityEngine;
using EventType = ShineEngine.EventType;

/// <summary>
/// UI逻辑体基类
/// </summary>
public class UILogicBase:SEventRegister
{
	/** 逻辑ID */
	public int id=-1;

	/** 逻辑体组 */
	private IntObjectMap<UILogicBase> _logicDic=new IntObjectMap<UILogicBase>();

	/** 父逻辑体 */
	private UILogicBase _parent;

	/** 是否初始化好(有资源才有) */
	private bool _inited=false;
	/** 是否析构了 */
	private bool _disposed=true;

	/** 是否跟随父窗口的显示而显示 */
	protected bool _isFollowParentShow=true;

	/** 当前是否显示 */
	private bool _isShow=false;

	/** 显示/隐藏 执行状态(0:未开始,1:执行中,2:完毕) */
	private int _executeState=0;

	/** 显示参数 */
	protected object[] _showArgs;

	/** 显示完毕回调 */
	private Action _showOverCall;

	/** 隐藏完毕回调 */
	private Action _hideOverCall;

	/** 初始化完毕回调组 */
	private SList<Action> _initOverCalls=new SList<Action>();

	private int _resizeIndex=-1;

	//加载相关
	/** 模型(基类) */
	protected UIModel _smodel;
	/** 逻辑体配置(可以为空) */
	protected UILogicConfig _logicConfig;

	/** 模型游戏对象 */
	protected GameObject _modelObject;

	/** 加载状态标记(0:未开始,1:加载中,2:完毕) */
	private int _loadState=0;

	/** 加载的资源 */
	private int _loadResource=-1;

	private int _loadVersion;

	/** timeOutIndex */
	private IntSet _timeOutIndexSet=new IntSet();
	/** intervalIndex */
	private IntSet _intervalIndexSet=new IntSet();
	/** frameIndex */
	private IntSet _frameIndexSet=new IntSet();
	/** fixedUpdateIndex */
	private IntSet _fixedUpdateIndexSet=new IntSet();
	/** updateIndex */
	private IntSet _updateIndexSet=new IntSet();
	
	public UILogicBase()
	{
		
	}

	/** 注册逻辑体 */
	protected virtual void registLogics()
	{

	}

	public void construct(int id)
	{
		this.id=id;

		if(UILogicConfig.getDic()!=null)
		{
			_logicConfig=UILogicConfig.get(id);
		}

		construct();
	}

	/** 构造 */
	public virtual void construct()
	{
		registLogics();

		UILogicBase[] values;
		UILogicBase v;

		for(int i=(values=_logicDic.getValues()).Length-1;i>=0;--i)
		{
			if((v=values[i])!=null)
			{
				v.construct();
			}
		}
	}

	/** 添加逻辑体 */
	public void addLogic(UILogicBase logic)
	{
		if(logic.id==-1)
		{
			Ctrl.print("逻辑体id未赋值:" + logic);
			return;
		}

		if(_logicDic.contains(logic.id))
		{
			Ctrl.print("已存在逻辑体:" + logic);
			return;
		}

		_logicDic.put(logic.id,logic);

		logic._parent=this;

		if(_inited)
		{
			logic.doInit();
		}
	}

	/** 移除逻辑体 */
	public void removeLogic(UILogicBase logic)
	{
		if(logic._parent!=this)
		{
			Ctrl.print("找不到逻辑体存在:" + logic);
			return;
		}

		_logicDic.remove(logic.id);

		logic._parent=null;

		logic.dispose();
	}

	/** 通过id取逻辑体 */
	public UILogicBase getLogic(int id)
	{
		return _logicDic.get(id);
	}

	/** 逻辑体字典 */
	public IntObjectMap<UILogicBase> getLogicDic()
	{
		return _logicDic;
	}

	/** 获取父对象 */
	public UILogicBase getParent()
	{
		return _parent;
	}

	/** 获取父UI对象 */
	public UIBase getParentUI()
	{
		return (UIBase)_parent;
	}

	/** 设置显示完毕回调 */
	public void setShowOverCall(Action func)
	{
		_showOverCall=func;
	}

	/** 设置隐藏完毕回调 */
	public void setHideOverCall(Action func)
	{
		_hideOverCall=func;
	}

	/** 添加初始化完毕回调 */
	public void addInitOverCall(Action func)
	{
		_initOverCalls.add(func);
	}

	/** 是否初始化完毕 */
	public bool inited
	{
		get {return _inited;}
	}

	/** 是否显示 */
	public bool isShow
	{
		get {return _isShow;}
	}

	/** 是否显示并初始化好(界面直调用) */
	public bool isShowAndInited
	{
		get {return _isShow && _inited;}
	}

	/** 是否跟随父显示 */
	public bool isFollowParentShow
	{
		get
		{
			if(_logicConfig!=null)
				return _logicConfig.isFollowParentShow;

			return _isFollowParentShow;
		}
	}

	public GameObject gameObject
	{
		get {return _modelObject;}
	}

	//接口组

	/** 预备初始化(系统用) */
	protected virtual void preInit()
	{

	}

	/** 初始化 */
	protected virtual void init()
	{

	}

	/** 析构 */
	protected virtual void dispose()
	{

	}

	/** 呈现 */
	protected virtual void onEnter()
	{

	}

	/** 退出 */
	protected virtual void onExit()
	{
	}

	/** 重置位置 */
	protected virtual void onResize()
	{
	}

	//实现组

	/** 执行初始化 */
	public void doInit()
	{
		if(_inited)
			return;

		preInit();

		try
		{
			init();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}

		_inited=true;

		SList<Action> initOverCalls=_initOverCalls;
		_initOverCalls=new SList<Action>();

		foreach(var v in initOverCalls)
		{
			if(v!=null)
			{
				v();
			}
		}

		this.dispatch(EventType.UIInit);
	}

	/** 执行进入 */
	private void doEnter()
	{
		if(!_inited)
			return;

		if(ShineSetting.needUIResize)
		{
			_resizeIndex=ViewControl.instance.addListener(EventType.Resize,doResize);
		}


		try
		{
			onEnter();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}

		this.dispatch(EventType.UIEnter);
	}

	private void doResize()
	{
		onResize();
	}

	private void doExit()
	{
		if(!_inited)
			return;

		if(ShineSetting.needUIResize)
		{
			ViewControl.instance.removeListener(EventType.Resize,_resizeIndex);
			_resizeIndex=-1;
		}

		try
		{
			if (_timeOutIndexSet.length() > 0)
			{
				_timeOutIndexSet.forEach(v =>
				{
					TimeDriver.instance.clearTimeOut(v);
				});
				
				_timeOutIndexSet.clear();
			}

			if (_intervalIndexSet.length() > 0)
			{
				_intervalIndexSet.forEach(v =>
				{
					TimeDriver.instance.clearInterval(v);
				});
				
				_intervalIndexSet.clear();
			}
			
			if (_frameIndexSet.length() > 0)
			{
				_frameIndexSet.forEach(v =>
				{
					TimeDriver.instance.clearFrame(v);
				});
				
				_frameIndexSet.clear();
			}
			
			if (_fixedUpdateIndexSet.length() > 0)
			{
				_fixedUpdateIndexSet.forEach(v =>
				{
					TimeDriver.instance.clearFixedUpdate(v);
				});
				
				_fixedUpdateIndexSet.clear();
			}
			
			if (_updateIndexSet.length() > 0)
			{
				_updateIndexSet.forEach(v =>
				{
					TimeDriver.instance.clearUpdate(v);
				});
				
				_updateIndexSet.clear();
			}
			
			onExit();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}

		this.dispatch(EventType.UIExit);
	}

	/** 执行析构 */
	public void doDispose()
	{
		//已析构
		if(_disposed)
			return;

		if(_logicDic.Count>0)
		{
			UILogicBase[] values;
			UILogicBase v;

			for(int i=(values=_logicDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					v.doDispose();
				}
			}

			//之后做临时Logic和常驻logic的区分
			// _logicDic.clear();
		}

		if(_inited)
		{
			_inited=false;

			try
			{
				dispose();
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}

		//析构完毕
		disposeOver();
	}

	protected virtual void disposeOver()
	{
		_disposed=true;

		if(_loadState>0)
		{
			if(_loadVersion==LoadControl.getVersion() && _loadResource>0)
			{
				//unload
				LoadControl.unloadOne(_loadResource);
			}

			if(_loadState==2)
			{
				if(_smodel!=null)
				{
					_smodel.doDispose();

					_smodel=null;

					GameObject.Destroy(_modelObject);
					_modelObject=null;
				}
			}
		}

		//归零
		_loadState=0;
	}

	/** 执行显示前 */
	protected virtual void beforeExecuteShow()
	{
		if(_logicConfig!=null && _logicConfig.resourceNameT>0)
		{
			//加载
			load(_logicConfig.resourceNameT);
		}
		else
		{
			executeShow();
		}
	}

	/** 加载 */
	protected void load(int resource)
	{
		//未加载好
		if(_smodel==null)
		{
			if(_loadState==0)
			{
				_loadState=1;
				_loadVersion=LoadControl.getVersion();
				_loadResource=resource;

				LoadControl.loadOne(resource,onLoadComplete,LoadPriorityType.UI);
			}
		}
		//已加载好
		else
		{
			onMakeComplete();
		}
	}

	/** 加载完毕 */
	private void onLoadComplete()
	{
		//(被析构了)
		if(_loadState==0)
		{
			return;
		}

		GameObject obj=LoadControl.getAsset(_loadResource);
		_modelObject=obj;

		if(ShineSetting.localLoadWithOutBundle)
		{
			UIControl.makeOnePrefab(obj);
		}

		initModelObject();

		if(isShow)
		{
			addShow();
		}
		else
		{
			removeShow();
		}

		_smodel=createModel();

		if(_smodel!=null)
		{
			_smodel.init(obj);
		}

		onMakeComplete();
	}

	/** 初始化加载好的gameObject(设置parent等等) */
	protected virtual void initModelObject()
	{

	}

	protected virtual UIModel createModel()
	{
		return null;
	}

	/** 构造完毕 */
	private void onMakeComplete()
	{
		_loadState=2;

		if(isShow)
		{
			executeShow();
		}
	}

	/** 执行显示 */
	protected void executeShow()
	{
		showDo();

		_executeState=1;

		showBegin();
	}

	protected void showDo()
	{
		doInit();
		doResize();
		doEnter();

		if(_logicDic.Count>0)
		{
			UILogicBase[] values;
			UILogicBase v;

			for(int i=(values=_logicDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.isFollowParentShow)
					{
						v.show();
					}
				}
			}
		}
	}

	/** 显示开始(可复写(全复写可做过程显示)) */
	protected void showBegin()
	{
		showOver();
	}

	/** 显示中断 */
	protected virtual void showCancel()
	{
	}

	protected void showOver()
	{
		_executeState=2;

		this.dispatch(EventType.UIShowOver);

		showOverEx();
		
		if (_showOverCall != null)
		{
			_showOverCall();
			_showOverCall = null;
		}
	}

	/** 附加方法 */
	protected virtual void showOverEx()
	{
	}

	/** 执行hide */
	private void executeHide()
	{
		hideDo();

		_executeState=1;

		hideBegin();
	}

	private void hideDo()
	{
		doExit();

		if(_logicDic.Count>0)
		{
			UILogicBase[] values;
			UILogicBase v;

			for(int i=(values=_logicDic.getValues()).Length-1;i>=0;--i)
			{
				if((v=values[i])!=null)
				{
					if(v.isFollowParentShow)
					{
						v.hide();
					}
				}
			}
		}
	}

	/** 隐藏开始 */
	protected void hideBegin()
	{
		hideOver();
	}

	/** 隐藏中断 */
	protected virtual void hideCancel()
	{
	}

	/** 隐藏结束 */
	protected virtual void hideOver()
	{
		removeShow();

		_executeState=2;

		hideOverEx();

		this.dispatch(EventType.UIHideOver);
		
		if (_hideOverCall != null)
		{
			_hideOverCall();
            _hideOverCall = null;
        }
	}

	protected virtual void hideOverEx()
	{

	}

	/** 添加显示 */
	protected virtual void addShow()
	{
		if(_modelObject!=null)
		{
			_modelObject.SetActive(true);
		}
	}

	/** 移除显示 */
	protected virtual void removeShow()
	{
		if(_modelObject!=null)
		{
			_modelObject.SetActive(false);
		}
	}

	protected virtual void preShow()
	{
		_disposed=false;
	}

	protected virtual void preHide()
	{

	}

	//终方法

	/** 显示 */
	public void show()
	{
		if(_isShow)
			return;

		_showArgs=null;

		toShow();
	}

	public void show(params object[] args)
	{
		if(_isShow)
			return;

		_showArgs=args;

		toShow();
	}

	private void toShow()
	{
		if(_executeState==1)
		{
			hideCancel();
		}

		_executeState=0;

		_isShow=true;

		preShow();

		addShow();

		beforeExecuteShow();
	}
	
	/** 隐藏 */
    public void hide()
    {
	    if(!_isShow)
		    return;

	    if(_executeState==1)
	    {
		    showCancel();
	    }

	    _showArgs=null;

	    preHide();

	    _isShow=false;

	    executeHide();
    }

	/** 显示或隐藏(显示了就隐藏，隐藏了就显示) */
	public void showOrHide()
	{
		if(_isShow)
			hide();
		else
			show();
	}

	/** 显示或刷新 */
	public void showOrRefresh()
	{
		if(_isShow)
		{
			if(_inited)
			{
				doExit();
				doEnter();
			}
		}
		else
		{
			show();
		}
	}
	
	/** 显示或刷新 */
	public void showOrRefresh(params object[] args)
	{
		_showArgs = args;
		
		if(_isShow)
		{
			if(_inited)
			{
				doExit();
				doEnter();
			}
		}
		else
		{
			show(args);
		}
	}

	protected int setTimeOut(Action func, int delay)
	{
		int index = TimeDriver.instance.setTimeOut(func, delay);
		if (index != -1)
		{
			_timeOutIndexSet.add(index);
		}
		return index;
	}

	protected void clearTimeOut(int index)
	{
		_timeOutIndexSet.remove(index);
		TimeDriver.instance.clearTimeOut(index);
	} 
	
	protected int setIntervalFixed(Action func, int delay)
	{
		int index = TimeDriver.instance.setIntervalFixed(func, delay);
		if (index != -1)
		{
			_intervalIndexSet.add(index);
		}
		return index;
	}

	protected int setInterval(Action<int> func, int delay)
	{
		int index = TimeDriver.instance.setInterval(func, delay);
		if (index != -1)
		{
			_intervalIndexSet.add(index);
		}
		return index;
	}
	
	protected void clearInterval(int index)
	{
		_intervalIndexSet.remove(index);
		TimeDriver.instance.clearInterval(index);
	} 
	
	protected int setFrame(Action<int> func)
	{
		int index = TimeDriver.instance.setFrame(func);
		if (index != -1)
		{
			_frameIndexSet.add(index);
		}
		return index;
	}
	
	protected void clearFrame(int index)
	{
		_frameIndexSet.remove(index);
		TimeDriver.instance.clearFrame(index);
	} 
	
	protected int setFixedUpdate(Action func)
	{
		int index = TimeDriver.instance.setFixedUpdate(func);
		if (index != -1)
		{
			_fixedUpdateIndexSet.add(index);
		}
		return index;
	}
	
	protected void clearFixedUpdate(int index)
	{
		_fixedUpdateIndexSet.remove(index);
		TimeDriver.instance.clearFixedUpdate(index);
	} 
	
	protected int setUpdate(Action func)
	{
		int index = TimeDriver.instance.setUpdate(func);
		if (index != -1)
		{
			_updateIndexSet.add(index);
		}
		return index;
	}
	
	protected void clearUpdate(int index)
	{
		_updateIndexSet.remove(index);
		TimeDriver.instance.clearUpdate(index);
	} 
}