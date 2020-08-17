package com.home.commonBase.scene.scene;

import com.home.commonBase.config.game.BattleConfig;
import com.home.commonBase.constlist.generate.BattleStateType;
import com.home.commonBase.data.scene.scene.BattleSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.scene.base.SceneLogicBase;
import com.home.commonBase.scene.base.Unit;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.net.base.BaseRequest;

/** 场景副本类玩法逻辑 */
public class SceneBattleLogic extends SceneLogicBase
{
	/** 副本配置 */
	protected BattleConfig _config;
	/** 副本状态 */
	protected int _state=BattleStateType.Wait;
	
	/** tick时间 */
	protected int _timeTick=-1;
	
	/** 正式开始经过秒数 */
	protected int _passSecond=0;
	
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
	
	@Override
	public void construct()
	{
	
	}
	
	@Override
	public void init()
	{
		//副本模式先关
		_scene.inout.ready=false;
	}
	
	@Override
	public void dispose()
	{
		_config=null;
		_state=BattleStateType.Wait;
		_timeTick=-1;
		_passSecond=0;
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
	
	public boolean checkCanEnter(long playerID)
	{
		if(!enabled)
			return true;
		
		//已结束
		if(_state==BattleStateType.End)
			return false;
		
		return true;
	}
	
	public void makeCharacterData(long playerID,UnitData data)
	{
		if(_config.isIndependent)
		{
			//CharacterUseLogic logic=player.character.getCurrentCharacterUseLogic();
			//
			//logic.cacheForTown();
			////清空
			//logic.getFightLogic().clearDataForIndependent();
		}
	}
	
	public void makeSceneEnterData(SceneEnterData data)
	{
		if(!enabled)
			return;
		
		BattleSceneData bData=BaseC.factory.createBattleSceneData();
		bData.state=_state;
		bData.timeTick=_timeTick;
		data.battleData=bData;
	}
	
	@Override
	public void onFrame(int delay)
	{
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
	
	@Override
	public void onSecond(int delay)
	{
		super.onSecond(delay);
		
		if(_state==BattleStateType.Running)
		{
			++_passSecond;
		}
	}
	
	public boolean canOperate()
	{
		if(!enabled)
			return true;
		
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
	public void onPlayerEnter(long playerID,Unit unit)
	{
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
	public void onPlayerLeave(long playerID,Unit unit)
	{
		if(unit!=null)
		{
			//死了给复活
			if(!unit.fight.isAlive())
			{
				unit.fight.doRevive();
			}
			
			if(_config.isIndependent)
			{
				//CharacterUseLogic logic=player.character.getCurrentCharacterUseLogic();
				//
				////同一个
				//if(logic.getData().id==unit.getUnitData().getCharacterIdentity().id)
				//{
				//	//读取缓存
				//	logic.loadTownCache();
				//}
			}
		}
		
	}
	
	/** 切换状态 */
	protected void switchState(int state)
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
				_scene.removeScene();
			}
				break;
		}
	}
	
	/** 推送切换状态 */
	protected void sendSwitchState()
	{
		BaseRequest request=createSendBattleStateRequest(_state,_timeTick);
		
		if(request!=null)
		{
			_scene.aoi.radioMessageAll(request);
		}
	}
	
	/** 是否运行状态 */
	protected boolean isRunning()
	{
		return _state==BattleStateType.Running;
	}
	
	/** 状态改变 */
	protected void onChangeState()
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
		if(_scene.inout!=null && _scene.inout.isPlayerAllExist())
		{
			if(_state==BattleStateType.Enter)
			{
				switchState(BattleStateType.Pre);
			}
		}
	}
	
	protected BaseRequest createSendBattleStateRequest(int state,int timeTick)
	{
		return null;
	}
}
