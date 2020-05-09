package com.home.commonClient.net.base;

import com.home.shine.bytes.BytesWriteStream;
import com.home.shine.global.ShineSetting;
import com.home.shine.net.base.BaseRequest;

//中心服转发消息
public class CenterRequest extends BaseRequest
{
	public CenterRequest()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}
	
	@Override
	protected void doWriteToStream(BytesWriteStream stream)
	{
		//直接协议号和内容
		stream.natureWriteUnsignedShort(_dataID);
		doWriteBytesSimple(stream);
	}
	
	///** 发送 */
	//public void send()
	//{
	//	//构造一下
	//	make();
	//
	//	CenterTransClientToGameRequest request=new CenterTransClientToGameRequest();
	//	request.setData(this);
	//	request.send();
	//}
}
