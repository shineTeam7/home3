package com.home.commonScene.global;

import com.home.commonScene.app.SceneApp;
import com.home.commonScene.control.SceneClientGMControl;
import com.home.commonScene.control.SceneFactoryControl;
import com.home.commonScene.control.SceneMainControl;
import com.home.commonScene.server.SceneServer;

public class SceneC
{
	/** app */
	public static SceneApp app;
	/** 工厂 */
	public static SceneFactoryControl factory;
	/** 主控制 */
	public static SceneMainControl main;
	/** 网络 */
	public static SceneServer server;
	/** 客户端GM */
	public static SceneClientGMControl clientGM;
}
