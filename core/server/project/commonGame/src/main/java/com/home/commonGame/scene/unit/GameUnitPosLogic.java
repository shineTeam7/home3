package com.home.commonGame.scene.unit;

import com.home.commonBase.constlist.generate.TaskType;
import com.home.commonBase.scene.base.Region;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;
import com.home.commonSceneBase.scene.unit.BUnitPosLogic;

public class GameUnitPosLogic extends BUnitPosLogic
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
}
