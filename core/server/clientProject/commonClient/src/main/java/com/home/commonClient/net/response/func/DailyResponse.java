package com.home.commonClient.net.response.func;
import com.home.commonClient.constlist.generate.GameResponseType;
import com.home.commonClient.net.base.GameResponse;
import com.home.shine.bytes.BytesReadStream;

public class DailyResponse extends GameResponse
{
	/** 数据类型ID */
	public static final int dataID=GameResponseType.Daily;
	
	public DailyResponse()
	{
		_dataID=GameResponseType.Daily;
	}
	
	/** 读取字节流(简版) */
	@Override
	protected void toReadBytesSimple(BytesReadStream stream)
	{
		super.toReadBytesSimple(stream);
		
	}
	
	/** 执行 */
	@Override
	protected void execute()
	{
		
	}
	
}
