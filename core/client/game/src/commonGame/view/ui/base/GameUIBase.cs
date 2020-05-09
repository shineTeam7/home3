using System;
using ShineEngine;

/// <summary>
/// UIBase二次封装
/// </summary>
public class GameUIBase:UIBase
{
	/// <summary>
	/// 玩家对象
	/// </summary>
	public Player me=GameC.player;

	/** 玩家消息注册 */
	private IntObjectMap<EventAction> _playerEventListenerDic=new IntObjectMap<EventAction>();

	/** 事件监听序号组 */
	private IntList _eventListenerList=new IntList();

	/** 是否需要调用li */
	protected bool _needCallListenerOnEnter=false;

	public GameUIBase()
	{
		
	}

	/** 注册玩家消息监听(初始化阶段用) */
	protected void registPlayerEventListener(int type,Action listener)
	{
		_playerEventListenerDic.put(type,EventAction.create(listener));
	}
	
	/** 注册玩家消息监听(初始化阶段用) */
	protected void registPlayerEventListener(int type,Action<object> listener)
	{
		_playerEventListenerDic.put(type,EventAction.create(listener));
	}

	/** 是否view界面 */
	public bool isView()
	{
		return _config.existType==UIExistType.View;
	}

	protected override void preShow()
	{
		base.preShow();

		if(isView())
		{
			GameC.ui.addUIView(this);
		}

		GameC.ui.onUIShow(this);
	}

	protected override void preHide()
	{
		base.preHide();

		if(isView())
		{
			GameC.ui.removeUIView(this);
		}

		GameC.ui.onUIHide(this);
	}

	protected override void onEnter()
	{
		base.onEnter();

		int[] keys=_playerEventListenerDic.getKeys();
		EventAction[] values=_playerEventListenerDic.getValues();
		int fv=_playerEventListenerDic.getFreeValue();
		int k;
		EventAction v;

		for(int i=keys.Length-1;i>=0;--i)
		{
			if((k=keys[i])!=fv)
			{
			    v=values[i];

				_eventListenerList.add(me.addListener(k,v));

				if(_needCallListenerOnEnter)
				{
					v.execute(null);
				}
			}
		}
	}

	protected override void onExit()
	{
		base.onExit();

		int[] values=_eventListenerList.getValues();

		for(int i=_eventListenerList.size()-1;i>=0;--i)
		{
		    me.removeListener(values[i]);
		}

		_eventListenerList.clear();
	}

	protected override void dispose()
	{
		base.dispose();

		int[] values=_eventListenerList.getValues();

		for(int i=_eventListenerList.size()-1;i>=0;--i)
		{
			me.removeListener(values[i]);
		}

		_eventListenerList.clear();
	}

	protected override void hideOver()
	{
		base.hideOver();

		if(_config.existType==UIExistType.Multi)
		{
			//回收
			GameC.ui.backMultiUI(this);
		}
	}

	protected void registCloseButton(UIButton button)
	{
		button.click=this.showOrHide;
	}
}