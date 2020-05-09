package com.home.commonClient.control;

import com.home.commonBase.config.game.RobotTestModeConfig;
import com.home.commonBase.global.BaseC;
import com.home.commonClient.global.ClientGlobal;
import com.home.commonClient.part.player.Player;

/** 行为控制 */
public class ClientBehaviourControl
{
	public void init()
	{
		ClientGlobal.config=RobotTestModeConfig.get(ClientGlobal.mode);
	}

	/** 玩家准备完毕(初次进入场景后/空场景) */
	public void onStart(Player player)
	{
		if(BaseC.constlist.robotTestMode_needLoginOut(ClientGlobal.mode))
		{
			player.system.testLogin();
		}
		
		if(BaseC.constlist.robotTestMode_needEnterScene(ClientGlobal.mode))
		{
			player.scene.testEnterScene();
		}
		
		//其他switch
	}
}
