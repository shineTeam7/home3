package com.home.commonClient.global;

import com.home.commonClient.app.ClientApp;
import com.home.commonClient.control.ClientBehaviourControl;
import com.home.commonClient.control.ClientFactoryControl;
import com.home.commonClient.control.ClientMainControl;
import com.home.commonClient.control.SceneControl;
import com.home.commonClient.server.ClientMainServer;

/** 客户端全局 */
public class ClientC
{
	/** app */
	public static ClientApp app;
	/** 工厂控制 */
	public static ClientFactoryControl factory;
	/** 主控制 */
	public static ClientMainControl main;
	/** 行为 */
	public static ClientBehaviourControl behaviour;
	/** 场景 */
	public static SceneControl scene;
	/** 客户端主服务 */
	public static ClientMainServer server;
}
