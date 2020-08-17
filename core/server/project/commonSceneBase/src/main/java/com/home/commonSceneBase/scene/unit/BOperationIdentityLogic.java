package com.home.commonSceneBase.scene.unit;

import com.home.commonBase.scene.unit.OperationIdentityLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshOperationStateRequest;

public class BOperationIdentityLogic extends OperationIdentityLogic
{
	@Override
	protected void sendRefreshState(int state)
	{
		_unit.radioMessage(RefreshOperationStateRequest.create(_unit.instanceID,state),true);
	}
}
