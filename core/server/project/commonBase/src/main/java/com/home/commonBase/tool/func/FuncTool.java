package com.home.commonBase.tool.func;

import com.home.commonBase.config.game.InfoCodeConfig;
import com.home.commonBase.data.func.FuncToolData;
import com.home.commonBase.logic.LogicEntity;
import com.home.shine.timer.DefaultTimeEntity;
import com.home.shine.timer.ITimeEntity;

/** 功能插件 */
public class FuncTool
{
	/** 插件类型 */
	private int _type;
	
	/** 功能ID */
	protected int _funcID;
	
	private FuncToolData _data;
	
	/** 是否后续添加 */
	public boolean isAdded=false;
	
	/** 主体 */
	protected LogicEntity _entity;
	
	public FuncTool(int type,int funcID)
	{
		_type=type;
		_funcID=funcID;
	}
	
	/** 插件类型 */
	public int getType()
	{
		return _type;
	}
	
	/** 获取功能ID */
	public int getFuncID()
	{
		return _funcID;
	}
	
	/** 设置逻辑主体 */
	public void setEntity(LogicEntity entity)
	{
		_entity=entity;
	}
	
	public LogicEntity getEntity()
	{
		return _entity;
	}
	
	/** 构造 */
	public void construct()
	{
	
	}
	
	/** 初始化 */
	public void init()
	{
	
	}
	
	/** 析构 */
	public void dispose()
	{
	
	}
	
	/** 每秒 */
	public void onSecond(int delay)
	{
	
	}
	
	/** 每分 */
	public void onMinute()
	{
	
	}
	
	/** 每日 */
	public void onDaily()
	{
	
	}

	/** 离线时间 */
	public void onOfflineTime(long delay)
	{

	}
	
	/** 新创建时 */
	public void onNewCreate()
	{
		_data.initDefault();
	}
	
	/** 重载配置 */
	public void onReloadConfig()
	{
	
	}
	
	/** 获取时间主体 */
	public ITimeEntity getTimeEntity()
	{
		return DefaultTimeEntity.instance;
	}
	
	/** 创建新数据 */
	protected FuncToolData createToolData()
	{
		return null;
	}
	
	/** 设置数据 */
	public final void setData(FuncToolData data)
	{
		if(data!=null)
		{
			data.funcID=_funcID;
			toSetData(data);
		}
		else
		{
			FuncToolData newData=createToolData();
			
			if(newData!=null)
			{
				newData.funcID=_funcID;
				toSetData(newData);
				onNewCreate();
			}
			else
			{
				//无需数据的Tool
			}
		}
	}
	
	public void clearData()
	{
		toSetData(null);
	}
	
	/** 获取数据 */
	public FuncToolData getData()
	{
		return _data;
	}
	
	public FuncToolData getNewCreateData()
	{
		setData(null);
		return _data;
	}
	
	protected void toSetData(FuncToolData data)
	{
		_data=data;
	}
	
	public void afterReadData()
	{
	
	}
	
	public void afterReadDataSecond()
	{
	
	}
	
	/** 警告日志 */
	public void warnLog(String str)
	{
		_entity.warnLog(str);
	}
	
	/** 警告日志 */
	public void warnLog(Object...args)
	{
		_entity.warnLog(args);
	}
	
	/** 错误日志 */
	public void errorLog(String str)
	{
		_entity.errorLog(str);
	}
	
	/** 错误日志 */
	public void errorLog(Object...args)
	{
		_entity.errorLog(args);
	}
	
	/** 推送信息码 */
	public void sendInfoCode(int code)
	{
		_entity.sendInfoCode(code);
	}
	
	/** 警告信息码(角色工具用) */
	public void warningInfoCode(int code)
	{
		warnLog(InfoCodeConfig.get(code).text);
		sendInfoCode(code);
	}
	
	/** 警告信息码(角色工具用) */
	public void warningInfoCode(int code,Object...args)
	{
		warnLog(InfoCodeConfig.get(code).text,args);
		sendInfoCode(code);
	}
}
