package com.home.commonBase.app;

import com.home.commonBase.constlist.generate.ServerMessageType;
import com.home.commonBase.control.BaseFactoryControl;
import com.home.commonBase.control.FactoryControl;
import com.home.commonBase.global.AppSetting;
import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.commonBase.server.BaseGameServer;
import com.home.commonBase.serverConfig.ServerNodeConfig;
import com.home.commonBase.tool.DataRegister;
import com.home.shine.ShineSetup;
import com.home.shine.agent.AgentControl;
import com.home.shine.control.BytesControl;
import com.home.shine.control.ThreadControl;
import com.home.shine.control.WatchControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineGlobal;
import com.home.shine.global.ShineSetting;
import com.home.shine.thread.BreakException;

/** App基类 */
public class App
{
	/** 唯一初始化 */
	private static boolean _initUnique=false;
	
	/** 编号 */
	public int id=0;
	/** 名字 */
	private String _name;
	
	private boolean _inited=false;
	
	/** 自定义启动回调 */
	private Runnable _customFunc;
	
	/** 工厂类 */
	protected FactoryControl _factory;
	/** 连接类 */
	protected BaseGameServer _server;
	
	public App(String name,int id)
	{
		_name=name;
		this.id=id;
	}
	
	/** 获取唯一名字 */
	public String getQName()
	{
		return _name+"_"+id;
	}

	/** 预备设置(java主线程) */
	protected void preInit()
	{
		//如果有存在的类，就先agent进来
		if(AgentControl.inited())
		{
			AgentControl.agentClass();
		}
		
		initBaseFactory();
	}
	
	protected void initBaseFactory()
	{
		if(BaseC.factory==null)
			BaseC.factory=createBaseFactoryControl();
	}

	/** 启动(java主线程) */
	public void start()
	{
		preInit();
		
		if(ShineSetting.isAllInOne)
		{
			ShineGlobal.processName="game";
		}
		else
		{
			ShineGlobal.processName=_name;
			
			if(id>0)
			{
				ShineGlobal.processName+="_" + id;
			}
		}
		
		//引擎启动
		ShineSetup.setup(this::preExit);
		
		ThreadControl.addMainFunc(this::preStartMain);
	}
	
	/** 自定义启动(java主线程) */
	public final void startCustom(Runnable func)
	{
		_customFunc=func;
		start();
	}
	
	/** 预备写入main */
	private void preStartMain()
	{
		//已退出
		if(ShineSetup.isExiting())
			return;
		
		try
		{
			startMain();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
			
			if(!(e instanceof BreakException))
			{
				ShineSetup.exit("启动过程出错");
			}
		}
	}
	
	private static synchronized void makeUnique()
	{
		if(!_initUnique)
		{
			_initUnique=true;
			makeBase();
		}
	}
	
	private void startMain()
	{
		_inited=true;
		
		makeUnique();
		
		makeControls();
		
		if(WatchControl.instance==null)
		{
			WatchControl.instance=_factory.createWatchControl();
		}
		
		//注册数据
		DataRegister dataRegister=_factory.createDataRegister();
		if(dataRegister!=null)
			dataRegister.regist();
		
		//TODO:之后弄配置到 数据中心
		
		//加载配置
		BaseC.config.init();
		
		if(_customFunc!=null)
		{
			_customFunc.run();
		}
		else
		{
			onStart();
		}
	}
	
	private static void makeBase()
	{
		//create
		BaseC.config=BaseC.factory.createConfigControl();
		BaseC.logic=BaseC.factory.createLogicControl();
		BaseC.constlist=BaseC.factory.createConstControl();
		BaseC.trigger=BaseC.factory.createTriggerControl();
		BaseC.push=BaseC.factory.createPushNotificationControl();
		BaseC.dbUpdate=BaseC.factory.createDBUpdateControl();
		BaseC.cls=BaseC.factory.createClassControl();
		
		//make
		
		BaseC.config.preInit();
		BaseC.trigger.init();
		BaseC.push.init();
		BaseC.cls.init();
		//注册
		BaseC.factory.createBaseDataRegister().regist();
	}
	
	/** 协议注册相关 */
	protected void messageRegist()
	{
	
	}
	
	/** 构造必需的control组 */
	protected void makeControls()
	{
	
	}
	
	/** 启动(主线程) */
	protected void onStart()
	{
		ServerNodeConfig.init();
	}
	
	/** 启动完毕 */
	public void startOver()
	{
		ThreadControl.watchStart();
		
		Ctrl.log(_name + this.id +" startOver");
	}
	
	private void preExit()
	{
		if(_inited)
		{
			onExit();
		}
		else
		{
			exitOver();
		}
	}
	
	/** 退出方法(主线程) */
	protected void onExit()
	{
		exitOver();
	}
	
	/** 退出下一步(主线程) */
	protected void exitOver()
	{
		Ctrl.log(_name + this.id +" exitOver");
		
		ShineSetup.exitOver();
	}
	
	//regist
	
	/** 主工厂控制 */
	protected FactoryControl createFactoryControl()
	{
		return new FactoryControl();
	}
	
	/** base工厂控制 */
	protected BaseFactoryControl createBaseFactoryControl()
	{
		return new BaseFactoryControl();
	}
	
	/** 测试 */
	public static void test(Runnable func)
	{
		ShineSetting.isRelease=false;
		AppSetting.init();
		
		new App("test",1)
		{
			@Override
			protected void onStart()
			{
				func.run();
			}
		}.start();
	}
}
