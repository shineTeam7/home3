package com.home.commonClient.scene.scene;

import com.home.commonBase.config.game.BattleConfig;
import com.home.commonBase.constlist.generate.BattleStateType;
import com.home.commonBase.constlist.generate.GameEventType;
import com.home.commonBase.data.scene.scene.BattleSceneEnterData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.shine.ctrl.Ctrl;

public class BattleScenePlayLogic extends GameScenePlayLogic
{
	protected BattleConfig _config;
	/** 副本状态 */
	private int _state=BattleStateType.Wait;
	
	/** tick时间 */
	private int _timeTick=-1;
	
	@Override
	public void init()
	{
		super.init();
		
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
	public void dispose()
	{
		super.dispose();
		
		_state=BattleStateType.Wait;
		_timeTick=-1;
		_config=null;
	}
	
	/** 设置副本ID */
	public void setBattleID(int id)
	{
		_config=BattleConfig.get(id);
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
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(_timeTick>0)
		{
			if((_timeTick-=delay)<=0)
			{
				_timeTick=-1;
				
				//				timeTickOut();
			}
		}
	}
	
	@Override
	public void initEnterData(SceneEnterData data)
	{
		super.initEnterData(data);
		
		BattleSceneEnterData bData=(BattleSceneEnterData)data;
		
		onRefreshBattleState(bData.state, bData.timeTick);
	}
	
	/** 刷新副本状态 */
	public void onRefreshBattleState(int state,int tick)
	{
		_state = state;
		_timeTick = tick;
		
		_gameScene.me.dispatch(GameEventType.BattleStateChange);
	}
}
