package com.home.commonSceneBase.net.sceneBaseRequest.unit;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.UnitSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 单位释放技能结束(强制结束)(generated by shine) */
public class UnitSkillOverRequest extends UnitSRequest
{
	/** 是否需要中断指令 */
	public boolean needBreak;
	
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.UnitSkillOver;
	
	public UnitSkillOverRequest()
	{
		_dataID=SceneBaseRequestType.UnitSkillOver;
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
		return "UnitSkillOverRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeBoolean(this.needBreak);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeBoolean(this.needBreak);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("needBreak");
		writer.sb.append(':');
		writer.sb.append(this.needBreak);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.needBreak=false;
	}
	
	/** 创建实例 */
	public static UnitSkillOverRequest create(int instanceID,boolean needBreak)
	{
		UnitSkillOverRequest re=(UnitSkillOverRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.needBreak=needBreak;
		return re;
	}
	
}
