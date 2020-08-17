using System;
using ShineEngine;

/// <summary>
///
/// </summary>
public class SceneServer:SceneBaseServer
{
	public override void initMessage()
	{
		base.initMessage();

		addRequestMaker(new SceneRequestMaker());
		addResponseMaker(new SceneResponseMaker());
		addClientRequestBind(new SceneRequestBindTool());
	}

	protected override void onConnect()
	{
		GameC.main.connectSceneSuccess();
	}

	protected override void onConnectFailed()
	{
		Ctrl.printForIO("连接scene失败一次");

		if(GameC.main.isRunning())
		{
			GameC.main.connectSceneFailed();
		}
	}

	protected override void onClose()
	{
		Ctrl.log("客户端scene连接断开");

		if(GameC.main.isRunning())
		{
			GameC.main.onSceneSocketClosed();
		}
	}
}