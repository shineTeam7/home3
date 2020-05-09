package com.home.commonTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.app.ClearAdaptersApp;
import com.home.shineTool.app.ConfigExportApp;
import com.home.shineTool.app.DBExportApp;
import com.home.shineTool.app.DBTruncateApp;
import com.home.shineTool.app.DataExportApp;
import com.home.shineTool.app.FixCppProjApp;
import com.home.shineTool.app.FixHotfixProjApp;
import com.home.shineTool.app.TriggerExportApp;
import com.home.shineTool.app.UIModelApp;
import com.home.shineTool.constlist.ExecuteReleaseType;

public class AllToolsApp
{
	public static void main(String[] args)
	{
		if(args.length<1)
		{
			return;
		}
		
		if(args[0].equals("mysqlSql"))
		{
			DBExportApp.executeQuery(args[1],args[2]);
			Ctrl.print("OK");
			return;
		}
		
		ShineToolSetup.init();
		
		switch(args[0])
		{
			case "all":
			{
				boolean isAll=args.length>1 && Boolean.parseBoolean(args[1]);
				int releaseType=args.length>2 ? Integer.parseInt(args[2]) : ExecuteReleaseType.Debug;
				
				new DataExportApp().executeAll(isAll,releaseType);
				new ConfigExportApp().executeAll(isAll,releaseType);
				new DBExportApp().executeAll(isAll);
			}
				break;
			//case "allClear":
			//{
			//	ShineToolSetting.isAllRefresh=true;
			//
			//	new DataExportApp().executeAll(true,ExecuteReleaseType.Debug);
			//	new ConfigExportApp().executeAll(true,ExecuteReleaseType.Debug);
			//	new DBExportApp().executeAll(true);
			//}
			//	break;
			case "dataExport":
			{
				int releaseType=args.length>2 ? Integer.parseInt(args[2]) : ExecuteReleaseType.Debug;
				
				new DataExportApp().executeAll(args.length>1 && Boolean.parseBoolean(args[1]),releaseType);
			}
				break;
			case "dbExport":
			{
				//boolean allClear=args.length>1 && Boolean.parseBoolean(args[1]);
				//new DBExportApp().executeAll(allClear);
				
				new DBExportApp().executeAll(true);
			}
				break;
			case "dbTruncate":
			case "truncate":
			{
				new DBTruncateApp().execute();
			}
				break;
			case "configExport":
			{
				int releaseType=args.length>2 ? Integer.parseInt(args[2]) : ExecuteReleaseType.Debug;
				
				new ConfigExportApp().executeAll(args.length>1 && Boolean.parseBoolean(args[1]),releaseType);
			}
				break;
			case "uiModel":
			{
				new UIModelApp().execute();
			}
				break;
			case "fixHotfixProj":
			{
				new FixHotfixProjApp().fix();
			}
				break;
			case "fixCppProj":
			{
				new FixCppProjApp().fix();
			}
				break;
			case "generateAdapters":
			{
				//废弃
				//GenerateAdaptersApp.main(null);
			}
				break;
			case "clearAdapters":
			{
				new ClearAdaptersApp().execute(args.length>1 && Boolean.parseBoolean(args[1]));
			}
				break;
			case "trigger":
			{
				new TriggerExportApp().executeAll(args.length>1 && Boolean.parseBoolean(args[1]));
			}
				break;
			case "exportNav":
			{
				ExportNavApp.doMapID(Integer.parseInt(args[1]));
			}
				break;
			default:
			{

			}
		}
	}
}
