package com.home.commonCenter.app;

import com.home.commonBase.app.App;
import com.home.commonBase.app.BaseGameApp;
import com.home.commonBase.data.login.CenterInitServerData;
import com.home.commonBase.data.login.GameInitServerData;
import com.home.commonCenter.constlist.generate.CenterRequestType;
import com.home.commonCenter.constlist.generate.CenterResponseType;
import com.home.commonCenter.control.CenterFactoryControl;
import com.home.commonCenter.global.CenterC;
import com.home.shine.control.BytesControl;
import com.home.shine.global.ShineSetting;

public class CenterApp extends BaseGameApp
{
	public CenterApp()
	{
		super("center",1);
		
		CenterC.app=this;
	}
	
	@Override
	protected void preInit()
	{
		super.preInit();
		
		if(!ShineSetting.isAllInOne)
		{
			//center自身线程数目修改
			ShineSetting.poolThreadNum=ShineSetting.ioThreadNum=ShineSetting.dbThreadNum=8;
		}
	}
	
	@Override
	protected void onStartNext()
	{
		super.onStartNext();
		
		//版本
		CenterC.version.init();
		//数据库
		CenterC.db.initWithAppID(CenterC.app.id);
		
		//main
		CenterC.main.init();
		//全局数据
		CenterC.global.load();
		//客户端GM
		CenterC.clientGM.init();
		//中心服事务
		CenterC.centerWork.init();
		//unit
		CenterC.scene.init();
		
		CenterC.logic.init();
		
		CenterC.db.initLast();
		
		//启动完毕
		startOver();
		
		CenterC.global.onStart();
	}
	
	/** 构造必需的control组 */
	protected void makeControls()
	{
		super.makeControls();
		
		CenterFactoryControl factory;
		_factory=factory=CenterC.factory=createFactoryControl();
		_server=CenterC.server=factory.createServer();
		
		CenterC.version=factory.createVersionControl();
		CenterC.db=factory.createDBControl();
		CenterC.main=factory.createMainControl();
		CenterC.global=factory.createGlobal();
		CenterC.scene=factory.createScene();
		CenterC.clientGM=factory.createClientGMControl();
		CenterC.centerWork=factory.createCenterWorkControl();
		CenterC.logic=factory.createCenterLogicControl();
	}
	
	@Override
	protected void onExit()
	{
		CenterC.server.noticeExit();
		
		checkExitNext();
	}
	
	/** 检查是否可进行下一步退出 */
	public void checkExitNext()
	{
		//没连接也没人了
		if(CenterC.server.isServerReceiveSocketEmpty())
		{
			exitNext();
		}
	}
	
	/** 退出下一步 */
	private void exitNext()
	{
		CenterC.server.dispose();
		
		CenterC.db.writeForExit(this::onWriteDBOver);
	}
	
	private void onWriteDBOver()
	{
		CenterC.main.dispose();
		
		CenterC.db.dispose();
		
		exitOver();
	}
	
	/** 工厂控制 */
	protected CenterFactoryControl createFactoryControl()
	{
		return new CenterFactoryControl();
	}
}
