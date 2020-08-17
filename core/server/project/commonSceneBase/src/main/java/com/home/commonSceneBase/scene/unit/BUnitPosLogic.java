package com.home.commonSceneBase.scene.unit;

import com.home.commonBase.constlist.generate.TaskType;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.base.Unit;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonSceneBase.net.sceneBaseRequest.unit.UnitSetPosDirRequest;

public class BUnitPosLogic extends UnitPosLogic
{
	@Override
	protected void sendSetPosDir(PosDirData posDir)
	{
		_unit.radioMessage(UnitSetPosDirRequest.create(_unit.instanceID,posDir),true);
	}
}
