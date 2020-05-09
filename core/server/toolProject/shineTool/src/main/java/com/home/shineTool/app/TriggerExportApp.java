package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.config.ConfigMakeTool;
import com.home.shineTool.tool.export.CommonDataExportTool;
import com.home.shineTool.tool.export.NormalConfigMakeTool;
import com.home.shineTool.tool.trigger.TriggerMakeTool;
import com.home.shineTool.utils.ToolFileUtils;

/** trigger导出 */
public class TriggerExportApp
{
	//common工程
	protected TriggerMakeTool _commonTool;
	//game工程
	protected TriggerMakeTool _gameTool;
	
	public TriggerExportApp()
	{
		_commonTool=new TriggerMakeTool();
		_commonTool.init(ProjectType.Common,ShineProjectPath.commonDataDirPath,
				null,ShineProjectPath.serverCommonBaseDirPath,CodeType.Java,
				null,ShineProjectPath.clientCommonGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/commonTriggerRecord.xml");
		
		
		_gameTool=new TriggerMakeTool();
		_gameTool.init(ProjectType.Game,ShineProjectPath.dataDirPath,
				ShineToolGlobal.serverSavePath + "/config",ShineProjectPath.serverBaseDirPath,CodeType.Java,
				ShineToolGlobal.clientSavePath + "/config",ShineProjectPath.clientGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/gameTriggerRecord.xml");
		_gameTool.setParentTool(_commonTool);
	}
	
	public void executeAll(boolean isAll)
	{
		ShineToolSetting.isAllRefresh=isAll;
		ShineToolSetting.isAll=isAll;
		
		CommonDataExportTool.makeShinePackage();
		
		//_commonTool.setStart(1);
		_commonTool.execute();
		
		//_gameTool.setStart(_commonTool.getEnd());
		_gameTool.execute();
		
		ShineToolSetting.isAllRefresh=false;
		ShineToolSetting.isAll=false;
		//倒表再
		ConfigExportApp.doConfig();
		//Ctrl.print("OK");
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
//		ShineToolSetting.isAllRefresh=true;
		//ShineToolSetting.needTrace=true;
		
		new TriggerExportApp().executeAll(true);
	}
}
