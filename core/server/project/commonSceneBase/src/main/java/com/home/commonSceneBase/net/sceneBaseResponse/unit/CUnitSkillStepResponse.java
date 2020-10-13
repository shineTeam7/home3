package com.home.commonSceneBase.net.sceneBaseResponse.unit;
import com.home.commonSceneBase.constlist.generate.SceneBaseResponseType;
import com.home.commonSceneBase.net.sceneBaseResponse.base.CUnitRResponse;
import com.home.shine.bytes.BytesReadStream;

/** 玩家单位技能步骤(generated by shine) */
public class CUnitSkillStepResponse extends CUnitRResponse
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseResponseType.CUnitSkillStep;
	
	public CUnitSkillStepResponse()
	{
		_dataID=SceneBaseResponseType.CUnitSkillStep;
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		unit.fight.clientSkillStep();
	}
	
	/** 获取数据类名 */
	@Override
	public String getDataClassName()
	{
		return "CUnitSkillStepResponse";
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