using System;
using ShineEngine;

/// <summary>
/// 功能插件
/// </summary>
public class FuncTool
{
	/** 功能ID */
	protected int _funcID;

	private int _type;

	private FuncToolData _data;

	public bool isAdded=false;

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

	public virtual void construct()
	{

	}

	/** 初始化 */
	public virtual void init()
	{

	}

	/** 析构 */
	public virtual void dispose()
	{

	}

	/** 每秒 */
	public virtual void onSecond(int delay)
	{

	}

	/** 新创建时 */
	public virtual void onNewCreate()
	{
		_data.initDefault();
	}

	/** 重载配置 */
	public virtual void onReloadConfig()
	{

	}

	/** 创建新数据 */
	protected virtual FuncToolData createToolData()
	{
		return null;
	}

	/** 设置数据 */
	public void setData(FuncToolData data)
	{
		if(data!=null)
		{
			data.funcID=_funcID;
			toSetData(data);
			afterReadData();
		}
		else
		{
			if(CommonSetting.isSingleGame)
			{
				FuncToolData newData=createToolData();

				if(newData!=null)
				{
					newData.funcID=_funcID;
					toSetData(newData);
					onNewCreate();
					afterReadData();
				}
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

	protected virtual void toSetData(FuncToolData data)
	{
		_data=data;
	}

	/** 读数据后 */
	public virtual void afterReadData()
	{

	}

	// /** 创建新数据 */
	// protected virtual FuncToolData createToolData()
	// {
	// 	return null;
	// }
	//
	// /** 新创建时 */
	// public virtual void onNewCreate()
	// {
	//
	// }
}