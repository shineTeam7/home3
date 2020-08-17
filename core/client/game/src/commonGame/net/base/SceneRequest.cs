using ShineEngine;

public class SceneRequest:BaseRequest
{
	public SceneRequest()
	{
		//客户端request
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
	}

	/// <summary>
	/// 发送
	/// </summary>
	public void send()
	{
		if(CommonSetting.isSingleGame)
			return;

		GameC.sceneServer.getSocket().send(this,true);
	}
}
