using System;
using ShineEngine;
using UnityEngine;
using UnityEngine.UI;

[Hotfix]
public class GameUIControl:SEventRegister
{
	/** ui字典(单例) */
	private IntObjectMap<GameUIBase> _uiDic=new IntObjectMap<GameUIBase>();

	/** 多实例ui池 */
	private IntObjectMap<ObjectPool<GameUIBase>> _multiUIPoolDic=new IntObjectMap<ObjectPool<GameUIBase>>();

	/** ui执行时刻字典 */
	private IntList[] _uiMoments=new IntList[UIDoMomentType.size];

	/** 当前存在的view */
	private UIBase _currentView;

	/** view栈 */
	private IntList _viewStack=new IntList();

	/** uiMask检测 */
	protected int _uiMaskCheckIndex = -1;

	/** 实际显示的UI组(GameObject加载出来的) */
	private SList<UIBase> _realShowUIList=new SList<UIBase>();

	public GameUIControl()
	{

	}
	
	public virtual void init()
	{

	}

	/// <summary>
	/// 注册ui(单例)
	/// </summary>
	public void registUI(GameUIBase uiBase)
	{
		_uiDic.put(uiBase.id,uiBase);
	}

	/// <summary>
	/// 注册ui(多实例)
	/// </summary>
	public void registMultiUI(int id,Func<GameUIBase> createFunc)
	{
		ObjectPool<GameUIBase> pool=new ObjectPool<GameUIBase>(createFunc,8);
		pool.setReleaseFunc(k=>k.doDispose());
		pool.setEnable(CommonSetting.viewUsePool);
		_multiUIPoolDic.put(id,pool);
	}

	/// <summary>
	/// 通过ID获取UI
	/// </summary>
	public GameUIBase getUIByID(int id)
	{
		return _uiDic.get(id);
	}

	/// <summary>
	/// 获取一个多实例UI
	/// </summary>
	public GameUIBase getMultiUI(int id)
	{
		ObjectPool<GameUIBase> pool=_multiUIPoolDic.get(id);

		if(pool==null)
		{
			Ctrl.throwError("找不到该id的多实例ui",id);
			return null;
		}

		return pool.getOne();
	}

	/// <summary>
	/// 回收多实例UI
	/// </summary>
	public void backMultiUI(GameUIBase ui)
	{
		_multiUIPoolDic.get(ui.id).back(ui);
	}
	
	/// <summary>
	/// 构造ui组
	/// </summary>
	public void makeUIs()
	{
		initUI();
		reigstMomentUI();
	}
	
	private void reigstMomentUI()
	{
		for(int i=0;i<UIDoMomentType.size;++i)
		{
			_uiMoments[i]=new IntList();
		}

		foreach(UIConfig config in UIConfig.getDic())
		{
			if(config!=null)
			{
				foreach(int v in config.doMoments)
				{
					_uiMoments[v].add(config.id);
				}
			}
		}
	}
	
	/// <summary>
	/// 初始化UI
	/// </summary>
	protected virtual void initUI()
	{

	}

	/// <summary>
	/// 隐藏所有UI(回登录口用)
	/// </summary>
	public void hideAllUI()
	{
		foreach(GameUIBase v in _uiDic)
		{
			v.hide();
		}

		//TODO:非单例UI的移除
	}

	/** 显示View */
	public void addUIView(UIBase view)
	{
		if(_currentView!=null)
		{
			_currentView.hide();
			_currentView=null;
		}

		_currentView=view;

		for(int i=0;i<_viewStack.length();++i)
		{
			if(_viewStack[i]==view.id)
			{
				//去掉后续的
				_viewStack.justSetSize(i);
				break;
			}
		}

		_viewStack.add(view.id);
	}

	/** 移除UIView */
	public void removeUIView(UIBase view)
	{
		if(_currentView==view)
		{
			_currentView=null;

			//退一个格
			_viewStack.justSetSize(_viewStack.size()-1);
		}
	}

	/** 回上一个view */
	public void backView()
	{
		if(_currentView==null)
			return;

		//没有栈了
		if(_viewStack.size()<=1)
			return;

		int uiID=_viewStack.get(_viewStack.size() - 2);

		getUIByID(uiID).show();
	}
	
	/// <summary>
	/// 移除场景的界面隐藏
	/// </summary>
	public virtual void hideUIByRemoveScene()
	{
		if(!ShineSetting.isWholeClient)
			return;

		UIBase ui;

		foreach(int v in _uiMoments[UIDoMomentType.RemoveSceneHide])
		{
			if((ui=getUIByID(v))!=null)
			{
				ui.hide();
			}
		}
	}
	
	/// <summary>
	/// 移除场景完毕的界面隐藏
	/// </summary>
	public virtual void hideUIByRemoveSceneOver()
	{
		if(!ShineSetting.isWholeClient)
			return;

		UIBase ui;

		foreach(int v in _uiMoments[UIDoMomentType.RemoveSceneOverHide])
		{
			if((ui=getUIByID(v))!=null)
			{
				ui.hide();
			}
		}
	}
	
	/// <summary>
	/// 进入场景的界面显示
	/// </summary>
	public virtual void showUIByEnterScene()
	{
		if(!ShineSetting.isWholeClient)
			return;

		UIBase ui;

		foreach(int v in _uiMoments[UIDoMomentType.EnterSceneHide])
		{
			if(checkShowUIConditions(UIConfig.get(v)))
			{
				if((ui=getUIByID(v))!=null)
				{
					ui.hide();
				}
			}
		}

		foreach(int v in _uiMoments[UIDoMomentType.EnterSceneShow])
		{
			if(checkShowUIConditions(UIConfig.get(v)))
			{
				if((ui=getUIByID(v))!=null)
				{
					ui.show();
				}
			}
		}


	}
	
	/// <summary>
	/// 进入场景完毕的界面显示
	/// </summary>
	public virtual void showUIByEnterSceneOver()
	{
		if(!ShineSetting.isWholeClient)
			return;

		UIBase ui;

		foreach(int v in _uiMoments[UIDoMomentType.EnterSceneOverShow])
		{
			if(checkShowUIConditions(UIConfig.get(v)))
			{
				if((ui=getUIByID(v))!=null)
				{
					ui.show();
				}
			}
		}
	}
	
	/// <summary>
	/// 离开场景(无场景)的UI显示
	/// </summary>
	public virtual void showUIByLeaveSceneOver()
	{
		if(!ShineSetting.isWholeClient)
			return;

		UIBase ui;

		foreach(int v in _uiMoments[UIDoMomentType.LeaveSceneOverShow])
		{
			if(checkShowUIConditions(UIConfig.get(v)))
			{
				if((ui=getUIByID(v))!=null)
				{
					ui.show();
				}
			}
		}
	}

	/// <summary>
	/// 检查UI显示条件
	/// </summary>
	public virtual bool checkShowUIConditions(UIConfig config)
	{
		return GameC.player.role.checkRoleConditions(config.showConditions,false);
	}

	/** alert确认弹框 */
	public virtual void alert(string msg,Action sureCall)
	{
		TimeDriver.instance.setTimeOut(sureCall,2000);
	}

	/** 标准提示(屏幕居中提示) */
	public virtual void notice(string msg)
	{

	}

	/** 显示网络延迟圈圈(isShow为false则为隐藏) */
	public virtual void showNetDelay(bool isShow)
	{
		Ctrl.print("显示网络圈圈",isShow);
	}

	/** 执行文字显示 */
	public virtual void showText(string str,int type)
	{
		switch(type)
		{
			case TextShowType.Notice:
			{
				notice(str);
			}
				break;
			case TextShowType.Alert:
			{
				alert(str,null);
			}
				break;
		}
	}
	
	/// <summary>
	/// 显示ui by uiType，手动调用
	/// </summary>
	public void showUIByType(int type,int id,params int[] args)
	{
		int[] jumpUIArgs;

		if (args != null)
		{
			jumpUIArgs=new int[2+args.Length];

			for (int i=0;i<args.Length;++i)
			{
				jumpUIArgs[i + 2] = args[i];
			}
		}
		else
		{
			jumpUIArgs=new int[2];
		}
		
		jumpUIArgs[0] = type;
		jumpUIArgs[1] = id;
		
		showUIByTypeArgs(jumpUIArgs);
	}

	/** 跳转ui参数,config配置调用 */
	public void showUIByTypeArgs(int[] jumpUIArgs)
	{
		if(!ShineSetting.isWholeClient)
			return;

		if (jumpUIArgs == null || jumpUIArgs.Length <1 )
		{
			return;
		}

		int type = jumpUIArgs[0];
		int id = -1;

		if (jumpUIArgs.Length > 1)
		{
			id = jumpUIArgs[1];
		}

		object[] args = null;

		if (jumpUIArgs.Length > 2)
		{
			args =new object[jumpUIArgs.Length-2];

			for (int i=2;i<jumpUIArgs.Length;++i)
			{
				args[i - 2] = jumpUIArgs[i];
			}
		}

		executeShowUIAction(type,id,args);
	}

	protected virtual void executeShowUIAction(int type,int id,object[] args)
	{
		switch (type)
		{
			case JumpUIType.Function:
			{
				if (UIConfig.functionIDToUIIDdic.contains(id))
				{
					toShowUIByTypeArgs(getUIByID(UIConfig.functionIDToUIIDdic.get(id)),args);
				}
			}
				break;
			case JumpUIType.Activity:
			{
				if (UIConfig.activityIDToUIIDdic.contains(id))
				{
					toShowUIByTypeArgs(getUIByID(UIConfig.activityIDToUIIDdic.get(id)),args);
				}
			}
				break;
		}
	}

	/** 执行ui显示 */
	public virtual void toShowUIByTypeArgs(GameUIBase ui,object[] args)
	{
		ui.show(args);
	}

	/** 添加实际显示 */
	public void addRealShow(UIBase ui)
	{
		Transform transform=ui.gameObject.transform;
		transform.SetAsLastSibling();
		_realShowUIList.add(ui);
		int pos=_realShowUIList.length() - 1;

		int layer=ui.getUIConfig().layer;

		UIBase[] values=_realShowUIList.getValues();

		UIBase temp;

		for(int i=_realShowUIList.length()-2;i>=0;--i)
		{
			if(values[i].getUIConfig().layer>layer)
			{
				temp=values[i];

				//插入
				transform.SetSiblingIndex(temp.gameObject.transform.GetSiblingIndex());

				//保持有序
				values[i]=ui;
				values[pos]=temp;
				pos=i;
			}
			else
			{
				break;
			}
		}

		GameObject mask = UIControl.getUIMask();

		if (mask== null)
			return;

		if(ui.getUIConfig().isModal)
		{
			mask.SetActive(true);
			//放前面
			mask.transform.SetSiblingIndex(transform.GetSiblingIndex()-1);
		}
	}

	/** 移除实际显示 */
	public void removeRealShow(UIBase ui)
	{
		_realShowUIList.removeObj(ui);

		if(ui.getUIConfig().isModal)
		{
			refreshUIMask();
		}
	}

	private void refreshUIMask()
	{
		GameObject mask = UIControl.getUIMask();

		if (mask== null)
			return;

		UIBase[] values=_realShowUIList.getValues();

		for(int i=_realShowUIList.length()-2;i>=0;--i)
		{
			if(values[i].getUIConfig().isModal)
			{
				mask.SetActive(true);
				//放前面
				mask.transform.SetSiblingIndex(values[i].gameObject.transform.GetSiblingIndex()-1);
				return;
			}
		}

		mask.transform.SetAsFirstSibling();
		mask.SetActive(false);
	}

	/** UI显示 */
	public virtual void onUIShow(GameUIBase ui)
	{

	}

	/** UI隐藏 */
	public virtual void onUIHide(GameUIBase ui)
	{

	}


	/** 获取当前ui */
	public IntObjectMap<GameUIBase> uiDic
	{
		get { return _uiDic; }
	}
	
	/** repoter Action */
	public virtual void repoterAction(int actionType)
	{
		//override define
	}
}
