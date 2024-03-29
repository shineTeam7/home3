package com.home.commonSceneBase.net.sceneBaseRequest.scene;
import com.home.commonSceneBase.constlist.generate.SceneBaseRequestType;
import com.home.commonSceneBase.net.sceneBaseRequest.base.SceneSRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 推送副本类场景状态信息(generated by shine) */
public class SendBattleStateRequest extends SceneSRequest
{
	/** 数据类型ID */
	public static final int dataID=SceneBaseRequestType.SendBattleState;
	
	/** 副本当前状态 */
	public int state;
	
	/** 剩余tick时间(-1就是没在tick) */
	public int timeTick;
	
	public SendBattleStateRequest()
	{
		_dataID=SceneBaseRequestType.SendBattleState;
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
		return "SendBattleStateRequest";
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
		writer.writeTabs();
		writer.sb.append("timeTick");
		writer.sb.append(':');
		writer.sb.append(this.timeTick);
		
		writer.writeEnter();
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeInt(this.state);
		
		stream.writeInt(this.timeTick);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeInt(this.state);
		
		stream.writeInt(this.timeTick);
		
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.state=0;
		this.timeTick=0;
	}
	
	/** 创建实例 */
	public static SendBattleStateRequest create(int state,int timeTick)
	{
		SendBattleStateRequest re=(SendBattleStateRequest)BytesControl.createRequest(dataID);
		re.state=state;
		re.timeTick=timeTick;
		return re;
	}
	
}
