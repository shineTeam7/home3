package com.home.commonGame.scene.scene;

import com.home.commonBase.data.scene.scene.SceneLocationData;
import com.home.commonBase.data.scene.unit.UnitData;
import com.home.commonGame.control.LogicExecutor;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.support.collection.LongSet;

/** 副本预进入逻辑 */
public class PreBattleScenePlayLogic extends GameSceneMethodLogic
{
	private int _lastTime;
	
	private LongSet _sureSet=new LongSet();
	
	/** 是否结束了 */
	private boolean _ended=false;
	
	@Override
	public void init()
	{
		super.init();
		
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		_sureSet.clear();
		_ended=false;
	}
	
	@Override
	public void endSigned()
	{
		super.endSigned();
		
		//加到set组
		for(long v:_gameScene.gameInOut.getSignedPlayerIDList())
		{
			_sureSet.add(v);
		}
	}
	
	@Override
	public void onFrame(int delay)
	{
		super.onFrame(delay);
		
		if(_lastTime>0)
		{
			if((_lastTime-=delay)<=0)
			{
				_lastTime=0;
				
				timeOut();
			}
		}
	}
	
	/** 设置剩余时间(秒) */
	public void setLastTime(int time)
	{
		_lastTime=time*1000;
	}
	
	private void timeOut()
	{
		allSure();
	}
	
	@Override
	public void makeScenePosData(UnitData data,int posID)
	{
		int index=_gameScene.gameInOut.getPlayerIndex(data.identity.playerID);

		if(index==-1)
		{
			index=0;
		}

		//直接设置x
		data.pos.pos.x=index;
	}
	
	/** 单位确认 */
	public void unitSure(long playerID)
	{
		_sureSet.remove(playerID);
		
		//够了
		if(_sureSet.isEmpty())
		{
			allSure();
		}
	}
	
	/** 全部确认 */
	private void allSure()
	{
		if(_ended)
			return;
		
		_ended=true;
		
		int sceneID=getEnterSceneID();
		
		if(sceneID<=0)
		{
			Ctrl.throwError("创建的场景ID为空");
		}
		
		LogicExecutor executor=(LogicExecutor)_scene.getExecutor();
		
		GameScene scene=executor.createScene(sceneID);
		SceneLocationData eData=scene.createLocationData();
		
		scene.gameInOut.setSignedPlayers(_gameScene.gameInOut.getSignedUnits());
		
		
		for(long v:_gameScene.gameInOut.getSignedPlayerIDList())
		{
			Player player=executor.getPlayer(v);
			
			if(player!=null)
			{
				//进入
				player.scene.playerEnterSignedSceneAndCheck(eData);
			}
			else
			{
				//TODO:补充进入
			}
		}
	}
	
	/** 获取要进入的场景ID */
	protected int getEnterSceneID()
	{
		return -1;
	}
}
