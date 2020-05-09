package com.home.commonGame.scene.unit;

import com.home.commonBase.data.item.ItemData;
import com.home.commonBase.scene.unit.UnitAICommandLogic;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameUnit;

public class GameUnitAICommandLogic extends UnitAICommandLogic
{
	@Override
	protected boolean doCanPickUpItem(ItemData item)
	{
		Player player=((GameUnit)_unit).getControlPlayer();
		
		if(player==null)
			return false;
		
		return player.bag.hasItemPlace(item);
	}
}
