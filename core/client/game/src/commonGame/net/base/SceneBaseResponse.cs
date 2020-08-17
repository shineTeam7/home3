using ShineEngine;

public abstract class SceneBaseResponse:BaseResponse
{
	/// <summary>
	/// me
	/// </summary>
	public Player me=GameC.player;

	public Scene scene;

	public SceneBaseResponse()
	{
		setNeedFullRead(ShineSetting.clientMessageUseFull);
		setNeedRelease();
	}

	protected override void preExecute()
	{
		if((scene=GameC.scene.getScene())==null)
		{
			Ctrl.warnLog("收到场景类消息时,场景不存在");
			return;
		}

		execute();
	}
}
