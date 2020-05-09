using System;
using ShineEngine;
using UnityEditor;
using UnityEngine;

/// <summary>
/// 主工程入口
/// </summary>
public class MainApp:App
{
	public MainApp()
	{
		GameC.mainApp=this;
	}

	//启动
	public void start(GameObject root)
	{
		ShineSetting.isEditor=false;
		//路径
		countIsRelease();

		initSetting();

		//引擎启动
		ShineSetup.setup(root,onExit);

		start();
	}

	public void startE()
	{
		long now=Ctrl.getTimer();

		//路径
		countIsRelease();
		initSetting();

		//基础客户端停掉
		if(!ShineSetting.isWholeClient)
			return;

		ShineSetup.setupForEditor();

		startForEditor();

		GameC.mainLogin.loadGameSync();

		Ctrl.print("editor就绪,耗时:"+(Ctrl.getTimer()-now)+"ms");
	}


	/** 启动 */
	protected override void onStart()
	{
		GameC.nativeUI.init();

		GameC.mainLogin.init();
		//本地存储初始化
		GameC.save.init();

		if(ShineSetting.isWholeClient)
		{
			//音频初始化(需要在本地存储初始化GameC.save.init之后执行)
			GameC.audio.init();
		}

		//开始
		GameC.mainLogin.start();
	}

	protected override void onStartForEditor()
	{
		GameC.mainLogin.init();
	}

	/** 退出方法 */
	protected virtual void onExit()
	{
		
	}

	/** 构造必需的control组 */
	protected override void makeControls()
	{
		MainFactoryControl factory=GameC.mainFactory=createFactoryControl();
		//先注册基础数据类型
		BytesControl.addDataMaker(new BaseDataMaker());

		GameC.nativeUI=factory.createGameNUIControl();
		GameC.mainLogin=factory.createLoginControl();
		GameC.save=factory.createLocalSaveControl();
		GameC.audio=factory.createAudioControl();
		GameC.ILRuntime=factory.createILRuntimeControl();

	}

	/** 基础工厂控制 */
	protected virtual MainFactoryControl createFactoryControl()
	{
		return new MainFactoryControl();
	}

	//app

	/** 计算是否正式版 */
	private static void countIsRelease()
	{
		bool isRelease=true;

		RuntimePlatform platform=Application.platform;

		if(platform==RuntimePlatform.WindowsEditor || platform==RuntimePlatform.OSXEditor)
		{
			isRelease=false;
		}
		else if(platform==RuntimePlatform.WindowsPlayer || platform==RuntimePlatform.OSXPlayer)
		{
			string path=Application.dataPath;

			//调试windows
			if(path.EndsWith("release/game_Data") || path.EndsWith("release/release_Data"))
			{
				isRelease=false;
			}

			//调试mac
			if(path.EndsWith("releaseMac/game.app/Contents") || path.EndsWith("releaseMac/release.app/Contents"))
			{
				ShineGlobal.sourcePath="/.." + ShineGlobal.sourcePath;
				isRelease=false;
			}
		}

		ShineSetting.isRelease=isRelease;
	}

	/** 初始化设置组 */
	protected virtual void initSetting()
	{
		// AppSetting.init();
	}
}