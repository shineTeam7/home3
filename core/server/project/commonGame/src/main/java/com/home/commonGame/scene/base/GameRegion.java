package com.home.commonGame.scene.base;

import com.home.commonBase.constlist.generate.RegionActionType;
import com.home.commonBase.scene.base.Unit;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.unit.GameCharacterIdentityLogic;
import com.home.commonSceneBase.scene.base.BRegion;

public class GameRegion extends BRegion
{
	@Override
	protected void doRegionAction(int[] args,Unit unit,boolean isEnter)
	{
		super.doRegionAction(args,unit,isEnter);
		
		switch(args[0])
		{
			case RegionActionType.EnterScene:
			{
				if(isEnter && unit.isCharacter())
				{
					//无需提示
					if(args.length<3 || args[2]==0)
					{
						Player player=((GameCharacterIdentityLogic)unit.identity).getPlayer();
						
						if(player!=null)
						{
							player.scene.applyEnterScene(args[1]);
						}
					}
				}
			}
				break;
		}
	}
}
