package com.home.commonClient.scene.scene;

import com.home.commonBase.constlist.generate.BattleStateType;
import com.home.commonBase.data.role.RoleShowData;
import com.home.commonBase.data.scene.scene.BattleSceneData;
import com.home.commonBase.data.scene.scene.SceneEnterData;
import com.home.commonBase.data.scene.scene.ScenePreInfoData;
import com.home.commonBase.scene.base.Scene;
import com.home.commonBase.scene.scene.SceneBattleLogic;
import com.home.commonClient.scene.base.GameScene;
import com.home.shine.support.collection.LongObjectMap;

public class GameSceneBattleLogic extends SceneBattleLogic
{
	private GameScene _gameScene;
	/** 指定的角色组 */
	private LongObjectMap<RoleShowData> _signedPlayers=new LongObjectMap<RoleShowData>();
	
	@Override
	public void setScene(Scene scene)
	{
		super.setScene(scene);
		_gameScene=(GameScene)scene;
	}
	
	public void initEnterData(SceneEnterData data)
	{
		if(!enabled)
			return;
		
		ScenePreInfoData preInfo=_gameScene.getPreInfo();
		
		if(preInfo!=null)
		{
			RoleShowData[] signedPlayers=_gameScene.getPreInfo().signedPlayers;
			
			if(signedPlayers!=null)
			{
				for(RoleShowData v : signedPlayers)
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
	
	/** 刷新副本状态(来自服务器) */
	public void onRefreshBattleStateByServer(int state,int tick)
	{
		_state = state;
		_timeTick = tick;
		
		onChangeState();
	}
}
