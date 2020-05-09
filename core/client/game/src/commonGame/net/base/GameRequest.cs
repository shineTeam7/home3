using ShineEngine;

/// <summary>
/// 逻辑服Request
/// </summary>
public class GameRequest:BaseRequest
{
	public GameRequest()
	{
		//客户端request
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}

	/// <summary>
	/// 发送
	/// </summary>
	public void send()
	{
		send(true);
	}
	
	/// <summary>
	/// 发送
	/// </summary>
	public void send(bool needLog)
	{
		if(CommonSetting.isSingleGame)
			return;

		GameC.server.getSocket().send(this,needLog);
	}
}