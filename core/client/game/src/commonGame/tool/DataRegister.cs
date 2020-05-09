using ShineEngine;

/// <summary>
/// 数据注册
/// </summary>
public class DataRegister
{
	public DataRegister()
	{
	}

	/// <summary>
	/// 添加一个
	/// </summary>
	protected void add(DataMaker maker)
	{
		BytesControl.addDataMaker(maker);
	}

	/// <summary>
	/// 注册
	/// </summary>
	public virtual void regist()
	{

	}
}