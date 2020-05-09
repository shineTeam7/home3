using System;
using ShineEngine;

/// <summary>
/// 游戏逻辑体基类
/// </summary
public class GameUILogicBase:UILogicBase
{
	/// <summary>
	/// 玩家数据
	/// </summary>
	public Player me=GameC.player;

	/** 玩家消息注册 */
	private IntObjectMap<EventAction> _playerEventListenerDic=new IntObjectMap<EventAction>();

	/** 事件监听序号组 */
	private IntList _eventListenerList=new IntList();

	//public CommonPlayer
	public GameUILogicBase()
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
}