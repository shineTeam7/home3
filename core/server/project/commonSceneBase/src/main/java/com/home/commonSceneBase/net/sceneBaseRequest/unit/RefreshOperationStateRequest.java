package com.home.commonSceneBase.net.sceneBaseRequest.unit;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.UnitSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 更新操作体状态消息(generated by shine) */
public class RefreshOperationStateRequest extends UnitSRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.RefreshOperationState;
	
	/** 状态 */
	public int state;
	
	public RefreshOperationStateRequest()
	{
		_dataID=SceneBaseRequestType.RefreshOperationState;
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
		return "RefreshOperationStateRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.state);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.state);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("state");
		writer.sb.append(':');
		writer.sb.append(this.state);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.state=0;
	}
	
	/** 创建实例 */
	public static RefreshOperationStateRequest create(int instanceID,int state)
	{
		RefreshOperationStateRequest re=(RefreshOperationStateRequest)BytesControl.createRequest(dataID);
		re.instanceID=instanceID;
		re.state=state;
		return re;
	}
	
}