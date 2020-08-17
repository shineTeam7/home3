package com.home.commonScene.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonBase.data.scene.scene.SceneServerExitData;
import com.home.commonScene.logic.unit.SCharacterUseLogic;
import com.home.commonScene.logic.unit.SMUnitFightDataLogic;
import com.home.commonScene.logic.unit.SMUnitUseLogic;
import com.home.commonScene.part.ScenePlayer;
import com.home.commonScene.scene.base.SScene;
import com.home.commonScene.server.SceneServer;
import com.home.shine.control.WatchControl;

/** 场景工厂 */
public class SceneFactoryControl extends FactoryControl
{
	@Override
	public WatchControl createWatchControl()
	{
		return new SceneWatchControl();
	}
	
	/** 主控制 */
	public SceneMainControl createMainControl()
	{
		return new SceneMainControl();
	}
	
	/** 客户端gm控制 */
	public SceneClientGMControl createClientGMControl()
	{
		return new SceneClientGMControl();
	}
	
	/** server */
	public SceneServer createServer()
	{
		return new SceneServer();
	}
	
	/** 创建场景player */
	public ScenePlayer createScenePlayer()
	{
		return new ScenePlayer();
	}
	
	/** 创建场景 */
	public SScene createScene()
	{
		return new SScene();
	}
	
	public SCharacterUseLogic createSCharacterUseLogic()
	{
		return new SCharacterUseLogic();
	}
	
	public SMUnitFightDataLogic createMUnitFightDataLogic()
	{
		return new SMUnitFightDataLogic();
	}
	
	public SceneServerExitData createSceneServerExitData()
	{
		return new SceneServerExitData();
	}
}
