package com.home.commonBase.baseData;

import com.home.shine.ctrl.Ctrl;
import com.home.shine.data.BaseData;

/** part基类 */
public abstract class BasePart
{
	/** 数据 */
	private BaseData _data;
	
	/** 设置数据 */
	public void setData(BaseData data)
	{
		_data=data;
	}
	
	/** 获取数据 */
	public final BaseData getData()
	{
		return _data;
	}
	
	/** 初始化给予数据(空数据) */
	public void newInitData()
	{
		BaseData data=createPartData();
		data.initDefault();
		setData(data);
	}
	
	/** 检查新增 */
	public void checkNewAdd()
	{
		//空读的
		if(_data==null || _data.isEmptyRead())
		{
			newInitData();
			onNewCreate();
		}
	}
	
	/** 构建partData(深拷) */
	public BaseData makePartData()
	{
		try
		{
			beforeMakeData();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		BaseData data=createPartData();
		
		if(_data==null)
		{
			Ctrl.errorLog("makePartData时,数据为空",data.getDataClassName());
		}
		else
		{
			try
			{
				data.copy(_data);
			}
			catch(Exception e)
			{
				Ctrl.errorLog(e);
			}
		}
		
		//深拷
		return data;
	}
	
	/** 构建partData(潜拷) */
	public BaseData makeShadowPartData()
	{
		try
		{
			beforeMakeData();
		}
		catch(Exception e)
		{
			Ctrl.errorLog(e);
		}
		
		//潜拷
		return _data;
	}
	
	/** 构造模块数据 */
	protected abstract BaseData createPartData();
	
	/** 构造数据前 */
	protected abstract void beforeMakeData();
	
	/** 构造函数(new过程) */
	public abstract void construct();
	
	/** 初始化(与dispose成对) */
	public abstract void init();
	
	/** 析构(与init成对) */
	public abstract void dispose();
	
	/** 新创建 */
	public abstract void onNewCreate();
	
	/** 读完数据后 */
	public abstract void afterReadData();
	
	/** 读数据后(第二阶段) */
	public void afterReadDataSecond()
	{
	
	}
	
	/** 每秒调用 */
	public void onSecond(int delay)
	{
		
	}
	
	/** 每分调用 */
	public void onMinute()
	{
	
	}
	
	/** 每天调用 */
	public void onDaily()
	{
	
	}
	
	/** 配置更新后 */
	public void onReloadConfig()
	{
	
	}
	
	/** 活动开启 */
	public void  onActivityOpen(int id,boolean atTime)
	{
	
	}
	
	/** 活动关闭 */
	public void onActivityClose(int id,boolean atTime)
	{
	
	}
	
	/** 活动重置 */
	public void onActivityReset(int id,boolean atTime)
	{
	
	}
}
