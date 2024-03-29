package com.home.commonSceneBase.net.sceneBaseRequest.unit;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.UnitSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 单位刷新buff剩余次数消息(generated by shine) */
public class UnitRefreshBuffLastNumRequest extends UnitSRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.UnitRefreshBuffLastNum;
	
	/** buff实例ID */
	public int buffInstanceID;
	
	/** 剩余次数 */
	public int lastNum;
	
	public UnitRefreshBuffLastNumRequest()
	{
		_dataID=SceneBaseRequestType.UnitRefreshBuffLastNum;
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
		return "UnitRefreshBuffLastNumRequest";
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("buffInstanceID");
		writer.sb.append(':');
		writer.sb.append(this.buffInstanceID);
		
		writer.writeEnter();
		writer.writeTabs();
		writer.sb.append("lastNum");
		writer.sb.append(':');
		writer.sb.append(this.lastNum);
		
		writer.writeEnter();
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.buffInstanceID);
		
		stream.writeInt(this.lastNum);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.buffInstanceID);
		
		stream.writeInt(this.lastNum);
		
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.buffInstanceID=0;
		this.lastNum=0;
	}
	
	/** 创建实例 */
	public static UnitRefreshBuffLastNumRequest create(int instanceID,int buffInstanceID,int lastNum)
	{
		UnitRefreshBuffLastNumRequest re=(UnitRefreshBuffLastNumRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.buffInstanceID=buffInstanceID;
		re.lastNum=lastNum;
		return re;
	}
	
}
