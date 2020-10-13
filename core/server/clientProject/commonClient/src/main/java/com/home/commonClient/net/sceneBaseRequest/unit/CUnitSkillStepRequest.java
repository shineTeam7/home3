package com.home.commonClient.net.sceneBaseRequest.unit;
import com.home.commonClient.constlist.generate.GameRequestType;
import com.home.commonClient.constlist.generate.SceneBaseRequestType;
import com.home.commonClient.net.sceneBaseRequest.base.CUnitRRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;

/** 玩家单位技能步骤(generated by shine) */
public class CUnitSkillStepRequest extends CUnitRRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.CUnitSkillStep;
	
	public CUnitSkillStepRequest()
	{
		_dataID=SceneBaseRequestType.CUnitSkillStep;
	}
	
	@Override
	protected void copyData()
	{
		super.copyData();
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "CUnitSkillStepRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.endWriteObj();
	}
	
	/** 创建实例 */
	public static CUnitSkillStepRequest create(int instanceID)
	{
		CUnitSkillStepRequest re=(CUnitSkillStepRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		return re;
	}
	
}