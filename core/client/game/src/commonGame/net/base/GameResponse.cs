using ShineEngine;

/// <summary>
/// 
/// </summary>
public abstract class GameResponse:BaseResponse
{
	/// <summary>
	/// me
	/// </summary>
	public Player me=GameC.player;

	public GameResponse()
	{
		setNeedFullRead(ShineSetting.clientMessageUseFull);
	}
}