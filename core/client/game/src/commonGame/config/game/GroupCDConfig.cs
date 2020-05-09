using ShineEngine;

/// <summary>
/// 组CD表
/// </summary>
public class GroupCDConfig:BaseConfig
{
	/** 存储集合 */
	private static GroupCDConfig[] _dic;

	/// <summary>
	/// id
	/// </summary>
	public int id;

	/// <summary>
	/// 冷却时间(ms)
	/// </summary>
	public int cd;

	/// <summary>
	/// 获取
	/// </summary>
	public static GroupCDConfig get(int id)
	{
		return _dic[id];
	}

	/// <summary>
	/// 设置字典
	/// </summary>
	public static void setDic(GroupCDConfig[] dic)
	{
		_dic=dic;
	}

	/// <summary>
	/// 获取全部
	/// </summary>
	public static GroupCDConfig[] getDic()
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

		cd=stream.readInt();
	}
}