using ShineEngine;

/// <summary>
/// 中心服http请求
/// </summary>
public abstract class LoginHttpRequest:BytesHttpRequest
{
	public LoginHttpRequest()
	{
		setNeedFullWrite(ShineSetting.clientMessageUseFull);
		_url=LocalSetting.loginHttpURL + "/" + ShineSetting.bytesHttpCmd;
	}
}