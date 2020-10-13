using ShineEngine;

/// <summary>
/// 建筑升级成功的广播协议 注意:包含父类参数 instanceID(generated by shine)
/// </summary>
public class BuildingLevelUpingCompleteResponse:UnitSResponse
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseResponseType.BuildingLevelUpingComplete;
	
	/// <summary>
	/// 下一等级
	/// </summary>
	public int level;
	
	public BuildingLevelUpingCompleteResponse()
	{
		_dataID=SceneBaseResponseType.BuildingLevelUpingComplete;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "BuildingLevelUpingCompleteResponse";
	}
	
	/// <summary>
	/// 读取字节流(完整版)
	/// </summary>
	protected override void toReadBytesFull(BytesReadStream stream)
	{
		base.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		this.level=stream.readInt();
		
		stream.endReadObj();
	}
	
	/// <summary>
	/// 读取字节流(简版)
	/// </summary>
	protected override void toReadBytesSimple(BytesReadStream stream)
	{
		base.toReadBytesSimple(stream);
		
		this.level=stream.readInt();
		
	}
	
	/// <summary>
	/// 转文本输出
	/// </summary>
	protected override void toWriteDataString(DataWriter writer)
	{
		base.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.Append("level");
		writer.sb.Append(':');
		writer.sb.Append(this.level);
		
		writer.writeEnter();
	}
	
	/// <summary>
	/// 执行
	/// </summary>
	protected override void execute()
	{
		
	}
	
	/// <summary>
	/// 回池
	/// </summary>
	protected override void toRelease(DataPool pool)
	{
		base.toRelease(pool);
		
		this.level=0;
	}
	
}