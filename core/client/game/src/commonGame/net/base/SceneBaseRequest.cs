using ShineEngine;

public class SceneBaseRequest:BaseRequest
{
	public SceneBaseRequest()
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

		if(CommonSetting.useSceneServer)
		{
			GameC.sceneServer.getSocket().send(this,needLog);
		}
		else
		{
			GameC.server.getSocket().send(this,needLog);
		}
	}
}
