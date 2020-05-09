package com.home.commonBase.control;

import com.home.commonBase.global.BaseC;
import com.home.commonBase.global.CommonSetting;
import com.home.shine.ShineSetup;
import com.home.shine.control.ThreadControl;
import com.home.shine.ctrl.Ctrl;
import com.home.shine.global.ShineSetting;
import com.home.shine.table.DBConnect;

/** DB操作 */
public abstract class BaseDBControl
{
	/** db链接 */
	private String _url;
	
	/** 数据库连接 */
	protected DBConnect _connect;
	
	protected boolean _inited=false;
	
	protected String _clsName;
	
	private int _keepIndex=-1;
	
	
	public BaseDBControl()
	{
		
	}
	
	/** 构造db文件路径 */
	protected void makeDBFilePath()
	{
	
	}
	
	/** 初始化 */
	public void init()
	{
		if(_inited)
			return;
		
		_clsName=this.getClass().getSimpleName();
		makeDBFilePath();
		
		long startTime=0L;
		
		if(ShineSetting.needDetailLog)
		{
			startTime=Ctrl.getTimer();
		}
		
		toInit();
		
		if(_connect==null)
		{
			return;
		}
		
		if(ShineSetup.isExiting())
		{
			_connect.close();
			_connect=null;
			
			return;
		}
		
		_inited=true;
		
		_keepIndex=ThreadControl.getMainTimeDriver().setInterval(this::onKeep,CommonSetting.dbKeepDelay * 1000);
		
		if(ShineSetting.needDetailLog)
		{
			Ctrl.runningLog("初始化数据库耗时:" + (Ctrl.getTimer() - startTime) + "ms");
		}
	}
	
	/** 执行初始化 */
	protected void toInit()
	{
		_connect=new DBConnect(getURL());
	}
	
	public void setURL(String value)
	{
		_url=value;
	}
	
	/** 获取db连接 */
	protected String getURL()
	{
		return _url;
	}
	
	/** 获取数据版本号 */
	protected int getDataVersion()
	{
		return BaseC.config.getDBDataVersion();
	}
	
	
	/** 析构 */
	public void dispose()
	{
		if(!_inited)
			return;
		
		_inited=false;
		
		toDispose();
	}
	
	protected void toDispose()
	{
		if(_keepIndex!=-1)
		{
			ThreadControl.getMainTimeDriver().clearInterval(_keepIndex);
			_keepIndex=-1;
		}
		
		if(_connect!=null)
		{
			_connect.close();
			_connect=null;
		}
	}
	
	/** 获取数据库连接 */
	public DBConnect getConnect()
	{
		return _connect;
	}
	
	/** keep间隔到了 */
	protected void onKeep(int delay)
	{
		_connect.keep();
	}
	
	
}
