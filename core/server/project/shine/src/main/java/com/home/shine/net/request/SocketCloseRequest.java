package com.home.shine.net.request;
import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.constlist.generate.ShineRequestType;
import com.home.shine.control.BytesControl;
import com.home.shine.net.base.ShineRequest;

/** 连接主动关闭(阻止重连)(generated by shine) */
public class SocketCloseRequest extends ShineRequest
{
	/** 数据类型ID */
	public static final int dataID=ShineRequestType.SocketClose;
	
	public SocketCloseRequest()
	{
		_dataID=ShineRequestType.SocketClose;
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
		return "SocketCloseRequest";
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
	public static SocketCloseRequest create()
	{
		SocketCloseRequest re=(SocketCloseRequest)BytesControl.createRequest(dataID);
		return re;
	}
	
}