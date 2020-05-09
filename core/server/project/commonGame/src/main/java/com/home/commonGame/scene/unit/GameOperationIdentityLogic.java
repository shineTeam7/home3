package com.home.commonGame.scene.unit;

import com.home.commonBase.scene.unit.OperationIdentityLogic;
import com.home.commonGame.net.request.scene.unit.RefreshOperationStateRequest;

public class GameOperationIdentityLogic extends OperationIdentityLogic
{
	protected void sendRefreshState(int state)
	{
		_unit.radioMessage(RefreshOperationStateRequest.create(_unit.instanceID,state),true);
	}
}
