using ShineEngine;

/// <summary>
/// 
/// </summary>
[Hotfix]
public class GameDataRegister:DataRegister
{
	public override void regist()
	{
		base.regist();

		add(new BaseDataMaker());

		add(new PlayerPartDataMaker());
		add(new PlayerListDataMaker());
	}
}