using ShineEngine;

/// <summary>
/// part基类
/// </summary>
[Hotfix]
public abstract class BasePart
{
	/** 数据 */
	private BaseData _data;

	public BasePart()
	{
	}

	/// <summary>
	/// 设置数据
	/// </summary>
	public virtual void setData(BaseData data)
	{
		_data=data;
	}

	/// <summary>
	/// 获得数据
	/// </summary>
	public BaseData getData()
	{
		return _data;
	}

	/// <summary>
	/// 初始化给予数据(空数据)(离线模式用)
	/// </summary>
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
			// onNewCreate();
		}
	}

	/** 构建partData(深拷) */
	public BaseData makePartData()
	{
		beforeMakeData();

		BaseData data=createPartData();

		if(_data==null)
		{
			Ctrl.errorLog("makePartData时,数据为空",data.getDataClassName());
		}
		else
		{
			data.copy(_data);
		}

		//深拷
		return data;
	}

	/** 构造模块数据 */
	protected virtual BaseData createPartData()
	{
		return null;
	}

	/** 构造数据前 */
	protected abstract void beforeMakeData();

	/// <summary>
	/// 构造(new过程)
	/// </summary>
	public abstract void construct();

	/// <summary>
	/// 初始化
	/// </summary>
	public abstract void init();

	/// <summary>
	/// 析构(回池)
	/// </summary>
	public abstract void dispose();

	/// <summary>
	/// 新创建(离线模式用)
	/// </summary>
	public abstract void onNewCreate();

	/// <summary>
	/// 读完数据后
	/// </summary>
	public abstract void afterReadData();

	/// <summary>
	/// 读完数据后
	/// </summary>
	public virtual void afterReadDataSecond()
	{

	}

	/// <summary>
	/// 登录前
	/// </summary>
	public virtual void beforeLogin()
	{

	}

	/// <summary>
	/// 每秒调用
	/// </summary>
	public virtual void onSecond(int delay)
	{

	}

	/// <summary>
	/// 每天调用
	/// </summary>
	public virtual void onDaily()
	{

	}

	/// <summary>
	/// 配置表更新后
	/// </summary>
	public virtual void onReloadConfig()
	{

	}
}