package com.home.commonSceneBase.scene.scene;

import com.home.commonBase.constlist.generate.BattleStateType;
import com.home.commonBase.scene.scene.SceneBattleLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.scene.SendBattleStateRequest;
import com.home.shine.net.base.BaseRequest;

public class BSceneBattleLogic extends SceneBattleLogic
{
	@Override
	protected BaseRequest createSendBattleStateRequest(int state,int timeTick)
	{
		return SendBattleStateRequest.create(state,timeTick);
	}
}
