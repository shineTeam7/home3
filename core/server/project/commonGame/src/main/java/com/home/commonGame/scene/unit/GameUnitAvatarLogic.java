package com.home.commonGame.scene.unit;

import com.home.commonBase.scene.unit.UnitAvatarLogic;
import com.home.commonGame.net.request.scene.unit.RefreshUnitAvatarPartRequest;
import com.home.commonGame.net.request.scene.unit.RefreshUnitAvatarRequest;
import com.home.shine.support.collection.IntIntMap;

public class GameUnitAvatarLogic extends UnitAvatarLogic
{
	@Override
	public void onAvatarChange(int modelID,IntIntMap dic)
	{
		super.onAvatarChange(modelID,dic);
		//主角的自己推
		_unit.radioMessage(RefreshUnitAvatarRequest.create(_unit.instanceID,modelID,dic),_unit.identity.isCUnitNotM());
	}
	
	@Override
	public void onAvatarPartChange(IntIntMap dic)
	{
		super.onAvatarPartChange(dic);
		//主角的自己推
		_unit.radioMessage(RefreshUnitAvatarPartRequest.create(_unit.instanceID,dic),_unit.identity.isCUnitNotM());
	}
}
