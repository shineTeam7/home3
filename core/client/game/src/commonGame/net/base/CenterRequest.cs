using System;
using ShineEngine;

/// <summary>
/// 中心服消息
/// </summary>
public class CenterRequest:BaseRequest
{
	public CenterRequest()
	{
		//关了检测
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}

	protected override void doWriteToStream(BytesWriteStream stream)
	{
		//直接协议号和内容
		stream.natureWriteUnsignedShort(_dataID);
		doWriteBytesSimple(stream);
	}

	/** 发送 */
	public void send()
	{
		GameC.player.sendCenter(this);
	}
}