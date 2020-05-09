package com.home.commonClient.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonBase.tool.DataRegister;
import com.home.commonClient.logic.team.PlayerTeamTool;
import com.home.commonClient.logic.union.PlayerUnionTool;
import com.home.commonClient.logic.unit.CharacterUseLogic;
import com.home.commonClient.logic.unit.PetUseLogic;
import com.home.commonClient.part.player.Player;
import com.home.commonClient.scene.base.GameScene;
import com.home.commonClient.server.ClientMainServer;
import com.home.commonClient.tool.ClientDataRegister;
import com.home.shine.control.WatchControl;

public class ClientFactoryControl extends FactoryControl
{
	@Override
	public DataRegister createDataRegister()
	{
		return new ClientDataRegister();
	}
	
	@Override
	public WatchControl createWatchControl()
	{
		return new ClientWatchControl();
	}
	
	public ClientMainControl createMainControl()
	{
		return new ClientMainControl();
	}
	
	public ClientBehaviourControl createBehaviourControl()
	{
		return new ClientBehaviourControl();
	}
	
	public SceneControl createSceneControl()
	{
		return new SceneControl();
	}
	
	public ClientMainServer createClientMainServer()
	{
		return new ClientMainServer();
	}
	
	//--逻辑组--//
	
	/** 创建角色 */
	public Player createPlayer()
	{
		return new Player();
	}
	
	/** 创建场景 */
	public GameScene createScene()
	{
		return new GameScene();
	}
	
	/** 创建角色使用逻辑 */
	public CharacterUseLogic createCharacterUseLogic()
	{
		return new CharacterUseLogic();
	}
	
	public PlayerTeamTool createPlayerTeamTool()
	{
		return new PlayerTeamTool();
	}
	
	public PlayerUnionTool createPlayerUnionTool()
	{
		return new PlayerUnionTool();
	}
	
	public PetUseLogic createPetUseLogic()
	{
		return new PetUseLogic();
	}
}
