package com.home.commonClient.net.sceneBaseResponse.unit.building;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;

/** 建筑建造成功(generated by shine) */
public class BuildingBuildCompleteResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.BuildingBuildComplete;
	
	public BuildingBuildCompleteResponse()
	{
		_dataID=SceneBaseResponseType.BuildingBuildComplete;
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "BuildingBuildCompleteResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		stream.endReadObj();
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		
	}
	
}