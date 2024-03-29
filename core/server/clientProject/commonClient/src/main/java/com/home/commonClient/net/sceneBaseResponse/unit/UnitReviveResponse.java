package com.home.commonClient.net.sceneBaseResponse.unit;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.constlist.generate.SceneBaseResponseType;
import com.home.commonClient.net.sceneBaseResponse.base.UnitSResponse;
import com.home.shine.bytes.BytesReadStream;

/** 单位复活消息(generated by shine) */
public class UnitReviveResponse extends UnitSResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.UnitRevive;
	
	public UnitReviveResponse()
	{
		_dataID=SceneBaseResponseType.UnitRevive;
	}
	
	/** 执行 */
	protected void execute()
	{
		scene.getFightUnitAbs(instanceID).fight.onRevive();
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "UnitReviveResponse";
	}
	
	/** 读取字节流(完整版) */
	@Override
	protected void toReadBytesFull(BytesReadStream stream)
	{
		super.toReadBytesFull(stream);
		
		stream.startReadObj();
		
		stream.endReadObj();
	}
	
}
