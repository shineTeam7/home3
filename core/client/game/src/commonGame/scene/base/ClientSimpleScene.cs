using System;
using ShineEngine;

/// <summary>
/// 客户端简版场景(单人)(只有load,show,play,camera)
/// </summary>
public class ClientSimpleScene:Scene
{
	protected override SceneInOutLogic createInOutLogic()
	{
		return null;
	}

	protected override ScenePosLogic createPosLogic()
	{
		return null;
	}

	protected override SceneShowLogic createShowLogic()
	{
		return null;
	}

	protected override SceneFightLogic createFightLogic()
	{
		return null;
	}
}