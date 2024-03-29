using ShineEngine;

/// <summary>
/// 客户端请求建筑升级 注意:包含父类参数 instanceID(generated by shine)
/// </summary>
public class CBuildingLevelUpRequest:CUnitRRequest
{
	/// <summary>
	/// 数据类型ID
	/// </summary>
	public const int dataID=SceneBaseRequestType.CBuildingLevelUp;
	
	public CBuildingLevelUpRequest()
	{
		_dataID=SceneBaseRequestType.CBuildingLevelUp;
	}
	
	/// <summary>
	/// 获取数据类名
	/// </summary>
	public override string getDataClassName()
	{
		return "CBuildingLevelUpRequest";
	}
	
	/// <summary>
	/// 写入字节流(完整版)
	/// </summary>
	protected override void toWriteBytesFull(BytesWriteStream stream)
	{
		base.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.endWriteObj();
	}
	
	protected override void copyData()
	{
		base.copyData();
	}
	
	/// <summary>
	/// 创建实例
	/// </summary>
	public static CBuildingLevelUpRequest create(int instanceID)
	{
		CBuildingLevelUpRequest re=(CBuildingLevelUpRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		return re;
	}
	
}
