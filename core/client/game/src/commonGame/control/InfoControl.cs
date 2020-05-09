using System;
using ShineEngine;

/// <summary>
/// 信息控制
/// </summary>
[Hotfix]
public class InfoControl
{
	/** 显示信息码(客户端用) */
	public void showInfoCode(int code)
	{
		toShowInfoCode(code,false,null);
	}

	/** 显示信息码(客户端用) */
	public void showInfoCode(int code,params string[] args)
	{
		toShowInfoCode(code,false,args);
	}

	/** 执行显示信息码 */
	public void toShowInfoCode(int code,bool fromServer,params string[] args)
	{
		InfoCodeConfig config=InfoCodeConfig.get(code);

		string str=StringUtils.replaceWithSign(config.text,args);

		if(fromServer)
		{
			Ctrl.print("收到服务器信息码",code,str);
		}

		GameC.ui.showText(str,config.showType);

		doInfoCode(code,str);
	}

	/** 执行特殊信息码 */
	protected virtual void doInfoCode(int code,string str)
	{
		switch(code)
		{
			case InfoCodeType.LoginGameFailed_dataVersionError:
			{
				alertForExit(str);
			}
				break;
			case InfoCodeType.LoginGameFailed_repeatLogin:
			case InfoCodeType.LoginGameFailed_playerAlreadyLogined:
			case InfoCodeType.LoginGameFailed_isBlock:
			case InfoCodeType.SwitchGameFailed_noSwitchData:
			case InfoCodeType.SwitchGameFailed_notRightState:

			case InfoCodeType.PlayerExit_crowedDown:
			case InfoCodeType.PlayerExit_beKicked:

			case InfoCodeType.LoginGameFailed_resourceVersionLow://版本过低也返回登录
			case InfoCodeType.OfflineWorkFailed:

			case InfoCodeType.LoginGameFailed_playerIDNotMatch:
			case InfoCodeType.LoginGameFailed_playerNotExist:
			{
				alertForBackLogin(str,false);
			}
				break;
			case InfoCodeType.PlayerExit_serverClose:
			case InfoCodeType.PlayerExit_socketClose:

			case InfoCodeType.LoginGameFailed_noLoginData:
			case InfoCodeType.LoginGameFailed_isLogining:

			{
				alertForBackLogin(str,true);
			}
				break;
			case InfoCodeType.PlayerExit_initiative:
			{
				GameC.main.setExit();
			}
				break;
			case InfoCodeType.ClientGMHelp:
			case InfoCodeType.ClientGMSuccess:
			{
				GMCommandUI gmCommandUi=GameC.nativeUI.getGMCommandUI();

				if (gmCommandUi != null)
				{
					gmCommandUi.onPrint(str);
				}

			}
				break;
			case InfoCodeType.ClientGMFailed:
			{
				GMCommandUI gmCommandUi=GameC.nativeUI.getGMCommandUI();

				if (gmCommandUi != null)
				{
					gmCommandUi.onPrint("<color=red>"+str+"</color>");
				}
			}
				break;
		}
	}

	/** 执行特殊信息码 */
	protected virtual void doInfoString(int type,string str)
	{

	}

	/** 弹框为退出 */
	protected virtual void alertForExit(string str)
	{
		Ctrl.print("需要退出",str);
		GameC.main.setExit();
		GameC.server.dispose();
		GameC.ui.alert(str,GameC.main.exit);
	}

	/** 弹框为返回登录 */
	protected virtual void alertForBackLogin(string str,bool canOfflineGame)
	{
		Ctrl.print("需要回登录",str);

		if(canOfflineGame && GameC.main.canOfflineMode())
		{
			//TODO:这里需要补充一个策略，就是是否直接连回原game服,应对与没有分服的游戏,或记录好初始原服

			GameC.server.close();
			GameC.main.enterOfflineMode();
		}
		else
		{
			GameC.main.setExit();
			GameC.server.dispose();
			GameC.ui.alert(str,GameC.main.backToLogin);
		}
	}
}