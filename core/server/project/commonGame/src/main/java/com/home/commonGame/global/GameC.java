package com.home.commonGame.global;

import com.home.commonGame.app.GameApp;
import com.home.commonGame.control.AreaWorkControl;
import com.home.commonGame.control.ClientGMControl;
import com.home.commonGame.control.ClientOfflineControl;
import com.home.commonGame.control.GameDBControl;
import com.home.commonGame.control.GameFactoryControl;
import com.home.commonGame.control.GameJoinControl;
import com.home.commonGame.control.GameLogControl;
import com.home.commonGame.control.GameLogicControl;
import com.home.commonGame.control.GameMainControl;
import com.home.commonGame.control.GameSwitchControl;
import com.home.commonGame.control.GameVersionControl;
import com.home.commonGame.control.PlayerWorkControl;
import com.home.commonGame.control.SceneControl;
import com.home.commonGame.part.gameGlobal.GameGlobal;
import com.home.commonGame.server.GameServer;

/** common区服控制组 */
public class GameC
{
	/** app */
	public static GameApp app;
	
	/** 版本控制 */
	public static GameVersionControl version;
	/** 工厂 */
	public static GameFactoryControl factory;
	/** 日志 */
	public static GameLogControl log;
	/** 数据库 */
	public static GameDBControl db;
	/** 主控制 */
	public static GameMainControl main;
	/** 跨服控制 */
	public static GameSwitchControl gameSwitch;
	/** server */
	public static GameServer server;
	/** 全局数据 */
	public static GameGlobal global;
	/** 场景控制 */
	public static SceneControl scene;
	/** 客户端GM */
	public static ClientGMControl clientGM;
	/** 角色事务控制 */
	public static PlayerWorkControl playerWork;
	/** 区服事务控制 */
	public static AreaWorkControl areaWork;
	/** 客户端离线模式 */
	public static ClientOfflineControl clientOffline;
	/** 合服控制 */
	public static GameJoinControl gameJoin;
	/** 逻辑控制 */
	public static GameLogicControl logic;
}
