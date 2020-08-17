using System;
using ShineEngine;

/// <summary>
/// 
/// </summary>
public class SceneBattleLogic:SceneLogicBase
{
	protected BattleConfig _config;
	/** 副本状态 */
	protected int _state=BattleStateType.Wait;

	/** tick时间 */
	private int _timeTick=-1;

	/** 正式开始经过秒数 */
	protected int _passSecond=0;

	/** 指定的角色组 */
	private LongObjectMap<RoleShowData> _signedPlayers=new LongObjectMap<RoleShowData>();

	public void preInit()
	{
		int battleID;

		if((battleID=_scene.getConfig().battleID)>0)
		{
			_config=BattleConfig.get(battleID);
			enabled=true;
		}
		else
		{
			enabled=false;
		}
	}

	public override void init()
	{
		base.init();

	}

	public override void preRemove()
	{
		base.preRemove();

		if(_scene.isDriveAll())
		{
			if(_scene.hero!=null&&!_scene.hero.fight.isAlive())
			{
				_scene.hero.fight.doRevive();
			}

			CharacterUseLogic logic=GameC.player.character.getCurrentCharacterUseLogic();

			if(_config.isIndependent)
			{
				//同一个
				if(logic.getData().id==_scene.hero.getUnitData().getCharacterIdentity().id)
				{
					//读取缓存
					logic.loadTownCache();
				}
			}
		}
	}

	public override void dispose()
	{
		base.dispose();

		_state=BattleStateType.Wait;
		_timeTick=-1;
		_config=null;
	}

	public bool canOperate()
	{
		if(!enabled)
			return true;

		return _state>=BattleStateType.Running;
	}

	public virtual void makeSceneEnterData(SceneEnterData data)
	{
		if(!enabled)
			return;

		BattleSceneData bData=GameC.factory.createBattleSceneData();
		bData.state=_state;
		bData.timeTick=_timeTick;
		data.battleData=bData;

		//units

		ScenePreInfoData preInfo=_scene.getPreInfo();

		if(preInfo!=null)
		{
			//指定进入角色构造
			RoleShowData[] signedPlayers=preInfo.signedPlayers;

			if(signedPlayers!=null)
			{
				foreach(RoleShowData v in signedPlayers)
				{
					//不是自己(有其他角色就自行创建)
					if(v.playerID!=data.hero.identity.playerID)
					{
						UnitData unitData=_scene.inout.createCharacterDataForRobot(v.playerID);

						data.units.add(unitData);
					}
				}
			}
		}
	}

	public BattleConfig getBattleConfig()
	{
		return _config;
	}

	/** 获取当前状态 */
	public int getState()
	{
		return _state;
	}

	/** 获取剩余时间 */
	public int getTimeTick()
	{
		return _timeTick;
	}

	public override void onFrame(int delay)
	{
		base.onFrame(delay);

		if(_timeTick>0)
		{
			if((_timeTick-=delay)<=0)
			{
				_timeTick=-1;

				if(_scene.isDriveAll())
				{
					timeTickOut();
				}
			}
		}
	}

	public override void onSecond()
	{
		base.onSecond();

		if(_state==BattleStateType.Running)
		{
			++_passSecond;
		}
	}

	/** 倒计时结束 */
	private void timeTickOut()
	{
		switch(_state)
		{
			case BattleStateType.Enter:
			{
				switchState(BattleStateType.Pre);
			}
				break;
			case BattleStateType.Pre:
			{
				switchState(BattleStateType.Running);
			}
				break;
			case BattleStateType.Running:
			{
				runningTimeOut();
			}
				break;
			case BattleStateType.Over:
			{
				switchState(BattleStateType.Result);
			}
				break;
			case BattleStateType.Result:
			{
				switchState(BattleStateType.End);
			}
				break;
		}
	}

	/** 运行状态时间到 */
	protected void runningTimeOut()
	{
		//TODO:到时间了是否成功可以读表

		doOver();
	}

	public void makeCharacterData(UnitData data)
	{
		if(!enabled)
			return;

		if(_scene.isDriveAll() &&  _config.isIndependent)
		{
			CharacterUseLogic logic=_scene.inout.getCharacterLogic(data.identity.playerID);

			//自己的才做
			if(logic.isSelf())
			{
				logic.cacheForTown();

				//清空
				logic.getFightLogic().clearDataForIndependent();
			}
		}
	}

	public virtual void makeScenePosData(UnitData data)
	{
		if(!enabled)
			return;

		//默认给第0个点
		int index=0;
		//初始位置
		int pid=_scene.getConfig().enterPosList[index];

		data.pos.setByFArr(_scene.getPlaceConfig().elements.get(pid).pos);
	}



	public void initEnterData(SceneEnterData data)
	{
		if(!enabled)
			return;

		ScenePreInfoData preInfo=_scene.getPreInfo();

		if(preInfo!=null)
		{
			RoleShowData[] signedPlayers=_scene.getPreInfo().signedPlayers;

			if(signedPlayers!=null)
			{
				foreach(RoleShowData v in signedPlayers)
				{
					_signedPlayers.put(v.playerID,v);
				}
			}
		}

		BattleSceneData bData=data.battleData;

		onRefreshBattleStateByServer(bData.state, bData.timeTick);

		if(_scene.isDriveAll())
		{
			//客户端场景,直接进入pre状态
			switchState(BattleStateType.Pre);
		}
	}

	/** 获取指定角色的单位信息 */
	public RoleShowData getSignedPlayer(long playerID)
	{
		return _signedPlayers.get(playerID);
	}

	/** 刷新副本状态(来自服务器) */
	public void onRefreshBattleStateByServer(int state,int tick)
	{
		_state = state;
		_timeTick = tick;

		onChangeState();
	}

	/** 切换状态(客户端主动) */
	private void switchState(int state)
	{
		_state=state;

		switch(state)
		{
			case BattleStateType.Pre:
			{
				if(_config.preStateTime<=0)
				{
					switchState(BattleStateType.Running);
				}
				else
				{
					_timeTick=_config.preStateTime * 1000;
					onChangeState();
				}
			}
				break;
			case BattleStateType.Running:
			{
				_timeTick=_config.runningStateTime * 1000;
				onChangeState();
			}
				break;
			case BattleStateType.Over:
			{
				onChangeState();

				_timeTick=_config.overStateTime * 1000;
			}
				break;
			case BattleStateType.Result:
			{
				onChangeState();

				_timeTick=_config.resultStateTime * 1000;
			}
				break;
			case BattleStateType.End:
			{
				onChangeState();

				//销毁
				_scene.removeScene();
			}
				break;
		}
	}

	/** 状态改变 */
	protected virtual void onChangeState()
	{
		switch(_state)
		{
			case BattleStateType.Pre:
			{
				onPre();
			}
				break;
			case BattleStateType.Running:
			{
				onStart();
			}
				break;
			case BattleStateType.Over:
			{
				onOver();
			}
				break;
			case BattleStateType.Result:
			{
				onResult();
			}
				break;
			case BattleStateType.End:
			{
				onEnd();
			}
				break;
		}

		GameC.player.dispatch(GameEventType.BattleStateChange);
	}

	protected virtual void onPre()
	{

	}

	protected virtual void onStart()
	{

	}

	protected virtual void onOver()
	{

	}

	protected virtual void onResult()
	{

	}

	protected virtual void onEnd()
	{

	}

	/** 执行结束 */
	protected void doOver()
	{
		if(_state==BattleStateType.Running)
		{
			switchState(BattleStateType.Over);
		}
	}
}