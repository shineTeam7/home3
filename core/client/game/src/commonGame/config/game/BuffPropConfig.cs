using ShineEngine;

/// <summary>
/// buff几率表
/// </summary>
public class BuffPropConfig:BaseConfig
{
	/** 存储集合 */
	private static BuffPropConfig[] _dic;

	/// <summary>
	/// 几率id
	/// </summary>
	public int id;

	/// <summary>
	/// 几率值(千分位)
	/// </summary>
	public int value;

	/// <summary>
	/// 获取
	/// </summary>
	public static BuffPropConfig get(int id)
	{
		return _dic[id];
	}

	/// <summary>
	/// 设置字典
	/// </summary>
	public static void setDic(BuffPropConfig[] dic)
	{
		_dic=dic;
	}

	/// <summary>
	/// 获取全部
	/// </summary>
	public static BuffPropConfig[] getDic()
	{
		return _dic;
	}

	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		id=stream.readInt();

		value=stream.readInt();
	}
}