using System;
using ShineEngine;

/// <summary>
/// 副本帧同步场景逻辑
/// </summary>
public class BattleSceneSyncPlayLogic:SceneMethodLogic
{
	/** 帧计时 */
	private int _frameTime=0;

	/** 缓冲组 */
	private SQueue<FrameSyncData> _cacheQueue=new SQueue<FrameSyncData>();

	private bool _waiting=false;

	/** 帧同步开始 */
	public void onFrameStart(SList<FrameSyncData> list)
	{
		//去掉头三帧

		list.forEach(k=>
		{
			_cacheQueue.offer(k);
		});

		_scene.setFrameSync(true);
	}

	/** 接收帧 */
	public void receiveFrame(FrameSyncData data)
	{
		_cacheQueue.offer(data);

		if(_waiting)
		{
			if(_cacheQueue.size()>=Global.frameSyncCacheFrames)
			{
				_waiting=false;
			}
		}

		while(_cacheQueue.Count>Global.frameSyncCacheFrames)
		{
			doFrame(ShineSetting.systemFrameDelay);
		}
	}

	/** 预备帧 */
	public void preFrame(int delay)
	{
		int t=_frameTime+delay;

		while(t>=ShineSetting.systemFrameDelay)
		{
			t-=ShineSetting.systemFrameDelay;
			doFrame(ShineSetting.systemFrameDelay);
		}

		_frameTime=t;
	}

	private void doFrame(int delay)
	{
		if(_waiting)
			return;

		if(_cacheQueue.isEmpty())
		{
			_waiting=true;
		}
		else
		{
			FrameSyncData data=_cacheQueue.poll();

			if(data.commands!=null && !data.commands.isEmpty())
			{
				data.commands.forEach(preExecuteCommand);
			}

			_scene.doFrame(delay);
		}
	}

	/** 预备执行指令 */
	protected virtual void preExecuteCommand(FrameSyncCommandData command)
	{
		Unit unit=_scene.getCharacterByIndex(command.index);

		if(unit==null)
		{
			Ctrl.throwError("帧同步出错");
			return;
		}

		executeCommand(unit,command);
	}

	/** 执行指令 */
	protected virtual void executeCommand(Unit unit,FrameSyncCommandData command)
	{

	}
}