namespace Shine
{
	export class InfoControl
	{
	 
		/** 显示信息码 */
		public showInfoCode(code:number,...args):void
		{
			this.toShowInfoCode(code,false,args);
		}
	
		/** 执行显示信息码 */
		public toShowInfoCode(code:number,fromServer:boolean,args:string[]):void
		{
			var config:InfoCodeConfig=InfoCodeConfig.get(code);
	
			var str:string=StringUtils.replaceWithSign(config.text,args);
	
			if(fromServer)
			{
				Ctrl.print("收到服务器信息码",code,str);
			}
			
			// GameC.ui.showText(str,config.showType);
			this.doShowInfoCode(str,config.showType);
			this.doInfoCode(code,str);
		}

		protected doShowInfoCode(str:string,type:number):void
		{

		}
	
		/** 执行特殊信息码 */
		protected doInfoCode(code:number,str:string):void
		{
			switch(code)
			{
				case InfoCodeType.LoginGameFailed_dataVersionError:
				{
					this.alertForExit(str);
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
					this.alertForBackLogin(str,false);
				}
					break;
				case InfoCodeType.PlayerExit_serverClose:
				case InfoCodeType.PlayerExit_socketClose:

				case InfoCodeType.LoginGameFailed_noLoginData:
				case InfoCodeType.LoginGameFailed_isLogining:

				{
					this.alertForBackLogin(str,true);
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
					// GameC.debug.getGMCommandUI().onPrint(str);
				}
					break;
				case InfoCodeType.ClientGMFailed:
				{
					// GameC.debug.getGMCommandUI().onPrint("<color=red>"+str+"</color>");
				}
					break;
			}
		}
	
		/** 执行特殊信息码 */
		protected  doInfoString(type:number,str:string):void
		{
	
		}
	
		/** 弹框为退出 */
		protected  alertForExit(str:string):void
		{
			Ctrl.print("需要退出",str);
			GameC.main.setExit();
			GameC.server.dispose();
			// GameC.ui.alert(str,GameC.main.exit);
		}
	
		/** 弹框为返回登录 */
		protected alertForBackLogin(str:string,canOfflineGame:boolean):void
		{
			Ctrl.print("需要回登录",str);
	
			if(CommonSetting.useOfflineGame && canOfflineGame)
			{
				GameC.server.close();
				GameC.main.enterOfflineMode();
			}
			else
			{
				GameC.main.setExit();
				GameC.server.dispose();
				// Ctrl.print("需要返回登陆界面!");
				// GameC.ui.alert(str,GameC.main.backToLogin);
			}
		}
	}
}