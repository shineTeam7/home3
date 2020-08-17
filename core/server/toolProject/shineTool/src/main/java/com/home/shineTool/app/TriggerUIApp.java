package com.home.shineTool.app;

import com.home.shine.ctrl.Ctrl;
import com.home.shineTool.ShineToolSetup;
import com.home.shineTool.tool.trigger.ui.TriggerMainUI;
import com.home.shineTool.tool.trigger.ui.TriggerUIDataExportTool;

/** trigger可视化编辑 */
public class TriggerUIApp
{
	public TriggerExportApp export;
	
	public TriggerMainUI mainUI;
	
	public TriggerUIDataExportTool uiDataTool;
	/** 执行 */
	public void execute()
	{
		long t=Ctrl.getTimer();
		//读取数据
		export=new TriggerExportApp();
		export.generateCode(false);
		export.readData();
		
		Ctrl.print("readTime:",(Ctrl.getTimer()-t));
		
		uiDataTool=new TriggerUIDataExportTool();
		
		//显示界面
		mainUI=new TriggerMainUI();
		mainUI.uiApp=this;
		mainUI.uiDataTool=uiDataTool;
		mainUI.init();
		
		uiDataTool.uiApp=this;
		uiDataTool.mainUI=mainUI;
		uiDataTool.init();
	}
	
	public static void main(String[] args)
	{
		ShineToolSetup.init();
		
		new TriggerUIApp().execute();
	}
}
