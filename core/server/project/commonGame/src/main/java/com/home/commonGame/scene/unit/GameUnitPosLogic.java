package com.home.commonGame.scene.unit;

import com.home.commonBase.constlist.generate.TaskType;
import com.home.commonBase.data.scene.base.PosDirData;
import com.home.commonBase.scene.base.Region;
import com.home.commonBase.scene.unit.UnitPosLogic;
import com.home.commonGame.net.request.scene.unit.UnitSetPosDirRequest;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;

public class GameUnitPosLogic extends UnitPosLogic
{
	@Override
	public void onEnterRegion(Region region)
	{
		super.onEnterRegion(region);
		
		if(region.getConfig().needTaskEvent && _unit.isCharacter())
		{
			Player player=((GameUnit)_unit).getPlayer();
			
			if(player!=null)
			{
				player.quest.taskEvent(TaskType.EnterRegion,_scene.getConfig().id,region.instanceID);
			}
		}
	}
	
	@Override
	protected void sendSetPosDir(PosDirData posDir)
	{
		_unit.radioMessage(UnitSetPosDirRequest.create(_unit.instanceID,posDir),true);
	}
}
