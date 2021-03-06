package com.home.commonGame.net.request.system;
import com.home.commonGame.constlist.generate.GameRequestType;
import com.home.commonGame.net.base.GameRequest;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.control.BytesControl;
import com.home.shine.support.DataWriter;
import com.home.shine.support.pool.DataPool;

/** 每日消息(generated by shine) */
public class DailyRequest extends GameRequest
{
	/** 下个每日时间 */
	public long nextDailyTime;
	
	/** 数据类型ID */
	public static final int dataID=GameRequestType.Daily;
	
	public DailyRequest()
	{
		_dataID=GameRequestType.Daily;
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
		return "DailyRequest";
	}
	
	/** 写入字节流(完整版) */
	@Override
	protected void toWriteBytesFull(BytesWriteStream stream)
	{
		super.toWriteBytesFull(stream);
		
		stream.startWriteObj();
		
		stream.writeLong(this.nextDailyTime);
		
		stream.endWriteObj();
	}
	
	/** 写入字节流(简版) */
	@Override
	protected void toWriteBytesSimple(BytesWriteStream stream)
	{
		super.toWriteBytesSimple(stream);
		
		stream.writeLong(this.nextDailyTime);
		
	}
	
	/** 转文本输出 */
	@Override
	protected void toWriteDataString(DataWriter writer)
	{
		super.toWriteDataString(writer);
		
		writer.writeTabs();
		writer.sb.append("nextDailyTime");
		writer.sb.append(':');
		writer.sb.append(this.nextDailyTime);
		
		writer.writeEnter();
	}
	
	/** 回池 */
	@Override
	protected void toRelease(DataPool pool)
	{
		super.toRelease(pool);
		
		this.nextDailyTime=0L;
	}
	
	/** 创建实例 */
	public static DailyRequest create(long nextDailyTime)
	{
		DailyRequest re=(DailyRequest)BytesControl.createRequest(dataID);
		re.nextDailyTime=nextDailyTime;
		return re;
	}
	
}
