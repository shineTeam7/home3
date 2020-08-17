package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.constlist.CodeType;
import com.home.shineTool.constlist.ProjectType;
import com.home.shineTool.global.ShineProjectPath;
import com.home.shineTool.global.ShineToolGlobal;
import com.home.shineTool.global.ShineToolSetting;
import com.home.shineTool.tool.export.CommonDataExportTool;
import com.home.shineTool.tool.trigger.TriggerDataExportTool;
import com.home.shineTool.tool.trigger.TriggerMakeTool;

/** trigger导出 */
public class TriggerExportApp
{
	//common工程
	public TriggerMakeTool commonTool;
	//game工程
	public TriggerMakeTool gameTool;
	/** 数据部分 */
	public TriggerDataExportTool dataTool;
	
	public TriggerExportApp()
	{
		commonTool=new TriggerMakeTool();
		commonTool.init(ProjectType.Common,ShineProjectPath.commonDataDirPath,
				null,ShineProjectPath.serverCommonBaseDirPath,CodeType.Java,
				null,ShineProjectPath.clientCommonGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/commonTriggerRecord.xml");
		
		
		gameTool=new TriggerMakeTool();
		gameTool.init(ProjectType.Game,ShineProjectPath.dataDirPath,
				ShineToolGlobal.serverSavePath + "/config",ShineProjectPath.serverBaseDirPath,CodeType.Java,
				ShineToolGlobal.clientSavePath + "/config",ShineProjectPath.clientGameDirPath,ShineToolSetting.getClientCodeType(),
				ShineToolGlobal.recordPath + "/gameTriggerRecord.xml");
		gameTool.setParentTool(commonTool);
		
		dataTool=new TriggerDataExportTool(gameTool);
		
		dataTool.setNeedTrace(false);
		dataTool.setFileRecordTool(gameTool.getRecordTool());
		dataTool.setInput(ShineProjectPath.dataDirPath+"/trigger/data","T",null,CodeType.Java);
		dataTool.setSavePath(ShineToolGlobal.configSavePath+"/trigger.xml");
		
		ShineToolSetting.isAllRefresh=true;
		ShineToolSetting.isAll=true;
	}
	
	/** 生成代码部分(并保留数据缓存) */
	public void generateCode(boolean needGenerate)
	{
		CommonDataExportTool.makeShinePackage();
		commonTool.needGenerateCode=needGenerate;
		gameTool.needGenerateCode=needGenerate;
		
		commonTool.execute();
		gameTool.execute();
	}
	
	/** 读取数据 */
	public void readData()
	{
		dataTool.execute();
	}
	
	public void executeAll()
	{
		ShineToolSetting.isAllRefresh=true;
		ShineToolSetting.isAll=true;
		
		generateCode(true);
		
		saveConfig();
	}
	
	public void saveConfig()
	{
		ShineToolSetting.isAllRefresh=true;
		ShineToolSetting.isAll=true;
		
		dataTool.execute();
		dataTool.writeBin();
		
		ShineToolSetting.isAllRefresh=false;
		ShineToolSetting.isAll=false;
		//倒表再
		ConfigExportApp.doConfig();
		
		ShineToolSetting.isAllRefresh=true;
		ShineToolSetting.isAll=true;
		
		Ctrl.print("OK");
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
//		ShineToolSetting.isAllRefresh=true;
		//ShineToolSetting.needTrace=true;
		
		new TriggerExportApp().executeAll();
	}
}
