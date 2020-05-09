using System.Reflection;

/// <summary>
/// common逻辑控制组
/// </summary>
public class GameC
{
	/// <summary>
	/// 主入口app
	/// </summary>
	public static MainApp mainApp;

	/// <summary>
	/// app
	/// </summary>
	public static GameApp app;

	/// <summary>
	/// 主工程工厂控制
	/// </summary>
	public static MainFactoryControl mainFactory;

	/// <summary>
	/// 工厂控制
	/// </summary>
	public static GameFactoryControl factory;

	/// <summary>
	/// 登录
	/// </summary>
	public static MainLoginControl mainLogin;

	/// <summary>
	/// 原始UI
	/// </summary>
	public static GameNatureUIControl nativeUI;

	/// <summary>
	/// 热更控制
	/// </summary>
	public static CILRuntimeControl ILRuntime;

	/// <summary>
	/// 音频控制
	/// </summary>
	public static AudioControl audio;

	/// <summary>
	/// 主控制
	/// </summary>
	public static GameMainControl main;


	/// <summary>
	/// 触摸控制
	/// </summary>
	public static TouchControl touch;

	/// <summary>
	/// server
	/// </summary>
	public static GameServer server;

	/// <summary>
	/// 角色
	/// </summary>
	public static Player player;

	/// <summary>
	/// ui控制
	/// </summary>
	public static GameUIControl ui;

	/// <summary>
	/// 场景控制
	/// </summary>
	public static SceneControl scene;

	/// <summary>
	/// 信息控制
	/// </summary>
	public static InfoControl info;

	/// <summary>
	/// 红点
	/// </summary>
	public static RedPointControl redPoint;

	/// <summary>
	/// 本地本地存储控制
	/// </summary>
	public static LocalSaveControl save;

	/// <summary>
	/// 角色本地存储控制
	/// </summary>
	public static PlayerSaveControl playerSave;

	/// <summary>
	/// 键盘控制
	/// </summary>
	public static KeyboardControl keyboard;

	/// <summary>
	/// 离线控制
	/// </summary>
	public static GameOfflineControl offline;

	/// <summary>
	/// 玩家版本控制
	/// </summary>
	public static PlayerVersionControl playerVersion;

	/// <summary>
	/// 日志控制
	/// </summary>
	public static GameLogControl log;

	/// <summary>
	/// 池化控制
	/// </summary>
	public static GamePoolControl pool;

	/// <summary>
	/// 客户端gm指令
	/// </summary>
	public static ClientGMControl clientGm;

	/// <summary>
	/// trigger控制
	/// </summary>
	public static TriggerControl trigger;

	/// <summary>
	/// 引导控制
	/// </summary>
	public static GuideControl guide;
}