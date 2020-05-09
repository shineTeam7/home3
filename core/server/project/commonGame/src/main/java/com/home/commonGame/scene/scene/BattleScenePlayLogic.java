package com.home.commonGame.scene.scene;

import com.home.commonBase.config.game.BattleConfig;
import com.home.commonBase.constlist.generate.BattleStateType;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.data.scene.scene.BattleSceneEnterData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonGame.logic.unit.CharacterUseLogic;
import com.home.commonGame.net.request.scene.scene.SendBattleStateRequest;
import com.home.commonGame.part.player.Player;
import com.home.shine.ctrl.Ctrl;

/** 副本类场景玩法逻辑 */
public class BattleScenePlayLogic extends GameScenePlayLogic
{
	/** 副本配置 */
	protected BattleConfig _config;
	/** 副本状态 */
	protected int _state=BattleStateType.Wait;
	
	/** tick时间 */
	private int _timeTick=-1;
	
	/** 正式开始经过秒数 */
	protected int _passSecond=0;
	
	@Override
	public SceneEnterData createSceneEnterData()
	{
		return new BattleSceneEnterData();
	}
	
	@Override
	public void onSetConfig()
	{
		int battleID;
		
		if((battleID=_scene.getConfig().battleID)>0)
		{
			_config=BattleConfig.get(battleID);
		}
		else
		{
			Ctrl.throwError("不能没有battleID");
		}
	}
	
	@Override
	public void init()
	{
		super.init();
		
		//副本模式先关
		_scene.inout.ready=false;
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_config=null;
		_state=BattleStateType.Wait;
		_timeTick=-1;
		_passSecond=0;
	}
	
	@Override
	public boolean checkCanEnter(Player player)
	{
		if(!super.checkCanEnter(player))
			return false;
		
		//已结束
		if(_state==BattleStateType.End)
			return false;
		
		return true;
	}
	
	@Override
	public void makeCharacterData(Player player,UnitData data)
	{
		super.makeCharacterData(player,data);
		
		if(_config.isIndependent)
		{
			CharacterUseLogic logic=player.character.getCurrentCharacterUseLogic();
			
			logic.cacheForTown();
			//清空
			logic.getFightLogic().clearDataForIndependent();
		}
	}
	
	@Override
	public void makeSceneEnterData(Player player,SceneEnterData data)
	{
		super.makeSceneEnterData(player,data);
		
		BattleSceneEnterData tData=(BattleSceneEnterData)data;
		tData.state=_state;
		tData.timeTick=_timeTick;
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(_timeTick>0)
		{
			if((_timeTick-=delay)<=0)
			{
				_timeTick=-1;
				timeTickOut();
			}
		}
	}
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(_state==BattleStateType.Running)
		{
			++_passSecond;
		}
	}
	
	@Override
	public boolean canOperate()
	{
		return _state>=BattleStateType.Running;
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
	
	/** 玩家角色进入 */
	public void onPlayerEnter(Player player,Unit unit)
	{
		super.onPlayerEnter(player,unit);
		
		if(_state==BattleStateType.Wait)
		{
			switchState(BattleStateType.Enter);
		}
		else if(_state==BattleStateType.Enter)
		{
			checkEnterState();
		}
	}
	
	/** 玩家角色离开 */
	public void onPlayerLeave(Player player,Unit unit)
	{
		super.onPlayerLeave(player,unit);
		
		if(unit!=null)
		{
			//死了给复活
			if(!unit.fight.isAlive())
			{
				unit.fight.doRevive();
			}
			
			if(_config.isIndependent)
			{
				CharacterUseLogic logic=player.character.getCurrentCharacterUseLogic();
				
				//同一个
				if(logic.getData().id==unit.getUnitData().getCharacterIdentity().id)
				{
					//读取缓存
					logic.loadTownCache();
				}
			}
		}
		
	}
	
	/** 切换状态 */
	private void switchState(int state)
	{
		_state=state;
		
		switch(state)
		{
			case BattleStateType.Enter:
			{
				_timeTick=_config.enterStateTime * 1000;
				
				onChangeState();
				checkEnterState();
			}
				break;
			case BattleStateType.Pre:
			{
				//标记就绪
				_scene.inout.ready=true;
				
				if(_config.preStateTime<=0)
				{
					switchState(BattleStateType.Running);
				}
				else
				{
					_timeTick=_config.preStateTime * 1000;
					
					sendSwitchState();
					onChangeState();
				}
			}
				break;
			case BattleStateType.Running:
			{
				_timeTick=_config.runningStateTime * 1000;
				
				sendSwitchState();
				onChangeState();
				
			}
				break;
			case BattleStateType.Over:
			{
				//前置,计算剩余时间
				sendSwitchState();
				onChangeState();
				
				_timeTick=_config.overStateTime * 1000;
			}
				break;
			case BattleStateType.Result:
			{
				//前置,计算剩余时间
				sendSwitchState();
				onChangeState();
				
				_timeTick=_config.resultStateTime * 1000;
			}
				break;
			case BattleStateType.End:
			{
				onChangeState();
				
				//销毁
				_gameScene.removeScene();
			}
				break;
		}
	}
	
	/** 推送切换状态 */
	private void sendSwitchState()
	{
		_scene.aoi.radioMessageAll(SendBattleStateRequest.create(_state,_timeTick));
	}
	
	/** 是否运行状态 */
	protected boolean isRunning()
	{
		return _state==BattleStateType.Running;
	}
	
	/** 状态改变 */
	private void onChangeState()
	{
		switch(_state)
		{
			case BattleStateType.Enter:
			{
				onEnter();
			}
				break;
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
	}
	
	/** 进入进入阶段 */
	protected void onEnter()
	{
	
	}
	
	/** 进入预备阶段 */
	protected void onPre()
	{
	
	}
	
	/** 进入正式开始阶段 */
	protected void onStart()
	{
	
	}
	
	/** 进入完成阶段 */
	protected void onOver()
	{
		//自定义胜利失败消息
	}
	
	/** 结算结果 */
	protected void onResult()
	{
		//自定义结果奖励消息
	}
	
	/** 结束 */
	protected void onEnd()
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
	
	protected void checkEnterState()
	{
		//人齐
		if(_gameScene.gameInOut!=null && _gameScene.gameInOut.isPlayerAllExist())
		{
			if(_state==BattleStateType.Enter)
			{
				switchState(BattleStateType.Pre);
			}
		}
	}
}
