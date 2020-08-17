using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class SceneBaseServer:BaseServer
{
	public override void init()
	{
		base.init();

		if(CommonSetting.clientOpenReconnect)
		{
			//开启重连
			getSocket().setOpenReconnect(true);
		}
	}

	public override void initMessage()
	{
		base.initMessage();

		addRequestMaker(new SceneBaseRequestMaker());
		addResponseMaker(new SceneBaseResponseMaker());
		addClientRequestBind(new SceneBaseRequestBindTool());
	}
}