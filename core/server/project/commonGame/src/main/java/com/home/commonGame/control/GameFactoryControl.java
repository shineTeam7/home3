package com.home.commonGame.control;

import com.home.commonBase.control.FactoryControl;
import com.home.commonGame.logic.team.PlayerTeamTool;
import com.home.commonGame.logic.team.TeamTool;
import com.home.commonGame.logic.union.PlayerUnionTool;
import com.home.commonGame.logic.union.UnionTool;
import com.home.commonGame.logic.unit.CharacterUseLogic;
import com.home.commonGame.logic.unit.MUnitFightDataLogic;
import com.home.commonGame.part.gameGlobal.GameGlobal;
import com.home.commonGame.part.player.Player;
import com.home.commonGame.scene.base.GameScene;
import com.home.commonGame.server.GameServer;
import com.home.commonGame.tool.func.GameSubsectionPageShowTool;
import com.home.commonGame.tool.func.GameToCenterPlayerSubsectionRankTool;
import com.home.commonGame.tool.func.PlayerEquipContainerTool;
import com.home.shine.control.WatchControl;

/** 游戏工厂方法(只创建类) */
public class GameFactoryControl extends FactoryControl
{
	//--control组--//
	
	@Override
	public WatchControl createWatchControl()
	{
		return new GameWatchControl();
	}
	
	/** 游戏版本控制 */
	public GameVersionControl createGameVersionControl()
	{
		return new GameVersionControl();
	}
	
	/** 角色版本控制 */
	public PlayerVersionControl createPlayerVersionControl()
	{
		return new PlayerVersionControl();
	}
	
	/** db */
	public GameDBControl createDBControl()
	{
		return new GameDBControl();
	}
	
	/** log */
	public GameLogControl createLogControl()
	{
		return new GameLogControl();
	}
	
	/** main */
	public GameMainControl createMainControl()
	{
		return new GameMainControl();
	}
	
	/** 跨服 */
	public GameSwitchControl createSwitchControl()
	{
		return new GameSwitchControl();
	}
	
	/** unit */
	public SceneControl createSceneControl()
	{
		return new SceneControl();
	}
	
	/** 创建Server */
	public GameServer createServer()
	{
		return new GameServer();
	}
	
	/** global */
	public GameGlobal createGlobal()
	{
		return new GameGlobal();
	}
	
	/** 客户端GM控制 */
	public ClientGMControl createClientGMControl()
	{
		return new ClientGMControl();
	}
	
	/** 离线事务 */
	public PlayerWorkControl createPlayerWorkControl()
	{
		return new PlayerWorkControl();
	}
	
	/** 区服事务 */
	public AreaWorkControl createAreaWorkControl()
	{
		return new AreaWorkControl();
	}
	
	/** 客户端离线模式 */
	public ClientOfflineControl createClientOfflineControl()
	{
		return new ClientOfflineControl();
	}
	
	/** 合服控制 */
	public GameJoinControl createGameJoinControl()
	{
		return new GameJoinControl();
	}
	
	/** 逻辑控制 */
	public GameLogicControl createGameLogicControl()
	{
		return new GameLogicControl();
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
	
	/** 创建工会工具 */
	public UnionTool createUnionTool()
	{
		return new UnionTool();
	}
	
	/** 创建玩家工会工具 */
	public PlayerUnionTool createPlayerUnionTool()
	{
		return new PlayerUnionTool();
	}
	
	/** 创建队伍工具 */
	public TeamTool createTeamTool()
	{
		return new TeamTool();
	}
	
	/** 创建玩家队伍工具 */
	public PlayerTeamTool createPlayerTeamTool()
	{
		return new PlayerTeamTool();
	}
	
	/** 创建玩家装备容器工具 */
	public PlayerEquipContainerTool createPlayerEquipContainerTool(int funcId)
	{
		return new PlayerEquipContainerTool(funcId);
	}

	/** 分组排行工具 */
	public GameToCenterPlayerSubsectionRankTool createGameToCenterPlayerSubsectionRankTool(int funcID, int maxNum, long valueMin)
	{
		return new GameToCenterPlayerSubsectionRankTool(funcID,maxNum,valueMin);
	}

	/** 分组排行翻页工具 */
	public GameSubsectionPageShowTool createGameSubsectionPageShowTool(int funcID, int showMaxNum, int eachPageShowNum)
	{
		return new GameSubsectionPageShowTool(funcID,showMaxNum,eachPageShowNum);
	}

	/** 创建主单位数据逻辑 */
	public MUnitFightDataLogic createMUnitFightDataLogic()
	{
		return new MUnitFightDataLogic();
	}
}
