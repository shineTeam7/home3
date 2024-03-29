package com.home.commonClient.net.sceneBaseRequest.base;
import com.home.commonClient.constlist.generate.GameRequestType;
import com.home.commonClient.constlist.generate.SceneBaseRequestType;
import com.home.commonClient.net.sceneBaseRequest.base.SceneRRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 控制单位操作消息(generated by shine) */
public class CUnitRRequest extends SceneRRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.CUnitR;
	
	/** 实例ID */
	public int instanceID;
	
	public CUnitRRequest()
	{
		_dataID=SceneBaseRequestType.CUnitR;
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
		return "CUnitRRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.instanceID);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.instanceID);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("instanceID");
		writer.sb.append(':');
		writer.sb.append(this.instanceID);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.instanceID=0;
	}
	
	/** 创建实例 */
	public static CUnitRRequest create(int instanceID)
	{
		CUnitRRequest re=(CUnitRRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		return re;
	}
	
}
