package com.home.commonClient.app;

import com.home.commonBase.app.App;
import com.home.commonBase.extern.ExternMethod;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonClient.control.ClientFactoryControl;
import com.home.commonClient.global.ClientC;
import com.home.commonClient.global.ClientGlobal;
import com.home.shine.ShineSetup;
import com.home.shine.control.ThreadControl;
import com.home.shine.global.ShineSetting;

public class ClientApp extends App
{
	/** 设置客户端release配置 */
	public static void setClientReleaseConfig()
	{
		ShineSetting.poolThreadNum=ClientGlobal.threadNum;
		ShineSetting.dbThreadNum=0;
	}
	
	private int _timeIndex=-1;
	
	private int _now=0;
	
	public ClientApp()
	{
		super("client",0);
		
		ClientC.app=this;
	}

	@Override
	protected void onStart()
	{
		//加载配置
		BaseC.config.init();
		//加载配置
		BaseC.config.initHotfix();
		
		if(CommonSetting.needExternLib)
		{
			loadLib();
		}
		
		ClientC.main.init();
		ClientC.server.init();
		
		ClientC.scene.init();
		ClientC.behaviour.init();
		
		
		
		startOver();
		startRun();
	}
	
	/** 构造必需的control组 */
	protected void makeControls()
	{
		ClientFactoryControl factory;
		_factory=factory=ClientC.factory=createFactoryControl();
		ClientC.main=factory.createMainControl();
		ClientC.behaviour=factory.createBehaviourControl();
		ClientC.scene=factory.createSceneControl();
		ClientC.server=factory.createClientMainServer();
	}
	
	@Override
	protected void onExit()
	{
		ClientC.main.dispose();
		ClientC.server.dispose();
		
		ShineSetup.exitOver();
	}
	
	protected void loadLib()
	{
		//加载lib
		ExternMethod.init();
	}
	
	//register
	
	//controls
	
	protected ClientFactoryControl createFactoryControl()
	{
		return new ClientFactoryControl();
	}
	
	/** 开始执行 */
	private void startRun()
	{
		_now=ClientGlobal.startNum;
		
		if(ClientGlobal.delay==0)
		{
			for(int i=0;i<ClientGlobal.num;++i)
			{
				ClientC.main.createClient(ClientGlobal.nameFront + (i+_now));
			}
		}
		else
		{
			_timeIndex=ThreadControl.getMainTimeDriver().setIntervalFixed(this::makeOne,ClientGlobal.delay);
		}
	}
	
	private void makeOne()
	{
		ClientC.main.createClient(ClientGlobal.nameFront + _now);
		++_now;
		
		if(_now >= ClientGlobal.num)
		{
			ThreadControl.getMainTimeDriver().clearInterval(_timeIndex);
			
			_timeIndex=-1;
		}
	}
	
	/** 在生成一个新玩家 */
	public void makeNewOne()
	{
		ThreadControl.getMainThread().addFunc(()->{
			ClientC.main.createClient(ClientGlobal.nameFront + _now);
			++_now;
		});
	}
}
