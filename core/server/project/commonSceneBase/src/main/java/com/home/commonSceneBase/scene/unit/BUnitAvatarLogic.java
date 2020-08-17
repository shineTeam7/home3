package com.home.commonSceneBase.scene.unit;

import com.home.commonBase.scene.unit.UnitAvatarLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAvatarPartRequest;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.RefreshUnitAvatarRequest;
import com.home.shine.support.collection.IntIntMap;

public class BUnitAvatarLogic extends UnitAvatarLogic
{
	@Override
	public void onAvatarChange(int modelID,IntIntMap dic)
	{
		super.onAvatarChange(modelID,dic);
		//主角的自己推
		_unit.radioMessage(RefreshUnitAvatarRequest.create(_unit.instanceID,modelID,dic),_unit.needRadioSelf());
	}
	
	@Override
	public void onAvatarPartChange(IntIntMap dic)
	{
		super.onAvatarPartChange(dic);
		//主角的自己推
		_unit.radioMessage(RefreshUnitAvatarPartRequest.create(_unit.instanceID,dic),_unit.needRadioSelf());
	}
}
